package ru.javawebinar.topjava.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.javawebinar.topjava.model.Role;
import ru.javawebinar.topjava.model.User;
import ru.javawebinar.topjava.repository.UserRepository;

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
    public User save(User user) {
        BeanPropertySqlParameterSource parameterSource = new BeanPropertySqlParameterSource(user);

        if (user.isNew()) {
            Number newKey = insertUser.executeAndReturnKey(parameterSource);
            user.setId(newKey.intValue());
        } else if (namedParameterJdbcTemplate.update("""
                   UPDATE users SET name=:name, email=:email, password=:password,
                   registered=:registered, enabled=:enabled, calories_per_day=:caloriesPerDay WHERE id=:id
                """, parameterSource) == 0) {
            return null;
        }
        jdbcTemplate.update("DELETE FROM user_roles WHERE user_id=?", user.getId());
        for (Role role : user.getRoles()) {
            jdbcTemplate.update("INSERT INTO user_roles (user_id, role) VALUES (?, ?)", user.getId(), role.name());
        }
        return user;
    }

    @Override
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
        List<User> result = jdbcTemplate.query(
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
        return result;
    }

    private User getRoles(User user) {
        if (user != null) {
// This works
//            List<String> roleStrings = jdbcTemplate.query("SELECT role FROM user_roles WHERE user_id=?", rs -> {
//                List<String> ls = new ArrayList<>();
//                while (rs.next()) {
//                    ls.add(rs.getString("role"));
//                }
//                return ls;
//            }, user.getId());

// This doesn't work!
//            var roleStrings = jdbcTemplate.query("SELECT role FROM user_roles  WHERE user_id=?", BeanPropertyRowMapper.newInstance(String.class),user.getId());
            List<String> roleStrings = jdbcTemplate.queryForList("SELECT role FROM user_roles WHERE user_id=?", String.class, user.getId());
            Set<Role> roles = new HashSet<>();
            for (String s : roleStrings) {
                roles.add(Role.valueOf(s));
            }
            user.setRoles(roles);
        }
        return user;
    }
}