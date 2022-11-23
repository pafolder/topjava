package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
@Transactional(readOnly = true)
public class JdbcUserRepository implements UserRepository {

    private static final BeanPropertyRowMapper<User> ROW_MAPPER = BeanPropertyRowMapper.newInstance(User.class);

    private final JdbcTemplate jdbcTemplate;

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private final SimpleJdbcInsert insertUser;

    @Autowired
    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.insertUser = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");

        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public User save(User user) {
        Set<ConstraintViolation<User>> violations = Validation.buildDefaultValidatorFactory().getValidator().validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);
        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else {
            if (namedParameterJdbcTemplate.update("""
                       UPDATE users SET name=:name, email=:email, password=:password,
                       registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                    """, parameterSource) == 0) {
                return null;
            } else {
                jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
            }
        }
        jdbcTemplate.batchUpdate("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i)
                    throws SQLException {
                ps.setLong(1, user.getId());
                ps.setString(2, user.getRoles().toArray(new Role[0])[i].name());
            }

            public int getBatchSize() {
                return user.getRoles().size();
            }
        });
        return user;
    }

    @Override
    @Transactional
    public boolean delete(int id) {
        return jdbcTemplate.update("DELETE FROM users WHERE id=?", id) != 0;
    }

    @Override
    public User get(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE id=?", ROW_MAPPER, id);
//        return getRoles(DataAccessUtils.singleResult(users));
        return getRoles(DataAccessUtils.singleResult(users));
    }

    @Override
    public User getByEmail(String email) {
//        return jdbcTemplate.queryForObject("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE email=?", ROW_MAPPER, email);
        return getRoles(DataAccessUtils.singleResult(users));
    }

    @Override
    public List<User> getAll() {
//        return jdbcTemplate.query("SELECT * FROM users ORDER BY name, email", ROW_MAPPER);
        return jdbcTemplate.query(
                "SELECT * FROM users LEFT JOIN user_roles roles on users.id = roles.user_id ORDER BY name, email",
                rs -> {
                    List<User> userList = new ArrayList<>();
                    User user = null;
                    while (rs.next()) {
                        Integer id = rs.getInt("id");
                        if (user == null || !id.equals(user.getId())) {
                            user = new User();
                            user.setId(id);
                            user.setName(rs.getString("name"));
                            user.setEmail(rs.getString("email"));
                            user.setPassword(rs.getString("password"));
                            user.setEnabled(rs.getBoolean("enabled"));
                            user.setCaloriesPerDay(rs.getInt("calories_per_day"));
                            String roleName = rs.getString("role");
                            if (roleName != null) {
                                user.setRoles(Set.of(Role.valueOf(roleName)));
                            } else {
                                user.setRoles(Collections.emptySet());
                            }
                            userList.add(user);
                        } else {
                            String roleName = rs.getString("role");
                            if (roleName != null) {
                                user.getRoles().add(Role.valueOf(roleName));
                            }
                        }
                    }
                    return userList;
                }
        );
    }

    @Override
    public User getWithMeals(int id) {
        List<User> users = jdbcTemplate.query("SELECT * FROM users WHERE users.id=?", ROW_MAPPER, id);
        List<Meal> meals = jdbcTemplate.query("SELECT * FROM meals WHERE meals.user_id=?", BeanPropertyRowMapper.newInstance(Meal.class), id);
        User user = getRoles(DataAccessUtils.singleResult(users));
        if (user != null) {
            user.setMeals(new HashSet<>(meals));
            return user;
        } else {
            return null;
        }
    }


    private User getRoles(User user) {
        if (user != null) {
            List<String> roleStrings = jdbcTemplate.queryForList("SELECT role FROM user_roles WHERE user_id=?", String.class, user.getId());
            Set<Role> roles = new HashSet<>();
            for (String roleString : roleStrings) {
                roles.add(Role.valueOf(roleString));
            }
            user.setRoles(roles);
        }
        return user;
    }
}