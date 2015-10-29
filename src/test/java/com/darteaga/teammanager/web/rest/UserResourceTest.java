package com.darteaga.teammanager.web.rest;

import com.darteaga.teammanager.Application;
import com.darteaga.teammanager.domain.User;
import com.darteaga.teammanager.repository.UserRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.darteaga.teammanager.domain.enumeration.Roles;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class UserResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    private static final String DEFAULT_USERNAME = "AAAAA";
    private static final String UPDATED_USERNAME = "BBBBB";
    private static final String DEFAULT_PASSWORD = "AAAAA";
    private static final String UPDATED_PASSWORD = "BBBBB";
    private static final String DEFAULT_EMAIL = "AAAAA";
    private static final String UPDATED_EMAIL = "BBBBB";

    private static final Integer DEFAULT_EDAD = 0;
    private static final Integer UPDATED_EDAD = 1;

    private static final DateTime DEFAULT_FECHA_NACIMIENTO = new DateTime(0L, DateTimeZone.UTC);
    private static final DateTime UPDATED_FECHA_NACIMIENTO = new DateTime(DateTimeZone.UTC).withMillisOfSecond(0);
    private static final String DEFAULT_FECHA_NACIMIENTO_STR = dateTimeFormatter.print(DEFAULT_FECHA_NACIMIENTO);

    private static final byte[] DEFAULT_DESCRIPTION = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DESCRIPTION = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_DESCRIPTION_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DESCRIPTION_CONTENT_TYPE = "image/png";


private static final Roles DEFAULT_ROLE = Roles.jugador;
    private static final Roles UPDATED_ROLE = Roles.entrenador;
    private static final String DEFAULT_POSISION = "AAAAA";
    private static final String UPDATED_POSISION = "BBBBB";

    @Inject
    private UserRepository userRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restUserMockMvc;

    private User user;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        UserResource userResource = new UserResource();
        ReflectionTestUtils.setField(userResource, "userRepository", userRepository);
        this.restUserMockMvc = MockMvcBuilders.standaloneSetup(userResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        user = new User();
        user.setUsername(DEFAULT_USERNAME);
        user.setPassword(DEFAULT_PASSWORD);
        user.setEmail(DEFAULT_EMAIL);
        user.setEdad(DEFAULT_EDAD);
        user.setFechaNacimiento(DEFAULT_FECHA_NACIMIENTO);
        user.setDescription(DEFAULT_DESCRIPTION);
        user.setRole(DEFAULT_ROLE);
        user.setPosision(DEFAULT_POSISION);
    }

    @Test
    @Transactional
    public void createUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        // Create the User

        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isCreated());

        // Validate the User in the database
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
        User testUser = users.get(users.size() - 1);
        assertThat(testUser.getUsername()).isEqualTo(DEFAULT_USERNAME);
        assertThat(testUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testUser.getEdad()).isEqualTo(DEFAULT_EDAD);
        assertThat(testUser.getFechaNacimiento().toDateTime(DateTimeZone.UTC)).isEqualTo(DEFAULT_FECHA_NACIMIENTO);
        assertThat(testUser.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testUser.getRole()).isEqualTo(DEFAULT_ROLE);
        assertThat(testUser.getPosision()).isEqualTo(DEFAULT_POSISION);
    }

    @Test
    @Transactional
    public void checkUsernameIsRequired() throws Exception {
        int databaseSizeBeforeTest = userRepository.findAll().size();
        // set the field null
        user.setUsername(null);

        // Create the User, which fails.

        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest());

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFechaNacimientoIsRequired() throws Exception {
        int databaseSizeBeforeTest = userRepository.findAll().size();
        // set the field null
        user.setFechaNacimiento(null);

        // Create the User, which fails.

        restUserMockMvc.perform(post("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isBadRequest());

        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllUsers() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get all the users
        restUserMockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(user.getId().intValue())))
                .andExpect(jsonPath("$.[*].username").value(hasItem(DEFAULT_USERNAME.toString())))
                .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD.toString())))
                .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
                .andExpect(jsonPath("$.[*].edad").value(hasItem(DEFAULT_EDAD)))
                .andExpect(jsonPath("$.[*].fechaNacimiento").value(hasItem(DEFAULT_FECHA_NACIMIENTO_STR)))
                .andExpect(jsonPath("$.[*].description").value(hasItem(Base64Utils.encodeToString(DEFAULT_DESCRIPTION))))
                .andExpect(jsonPath("$.[*].role").value(hasItem(DEFAULT_ROLE.toString())))
                .andExpect(jsonPath("$.[*].posision").value(hasItem(DEFAULT_POSISION.toString())));
    }

    @Test
    @Transactional
    public void getUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

        // Get the user
        restUserMockMvc.perform(get("/api/users/{id}", user.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(user.getId().intValue()))
            .andExpect(jsonPath("$.username").value(DEFAULT_USERNAME.toString()))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
            .andExpect(jsonPath("$.edad").value(DEFAULT_EDAD))
            .andExpect(jsonPath("$.fechaNacimiento").value(DEFAULT_FECHA_NACIMIENTO_STR))
            .andExpect(jsonPath("$.description").value(Base64Utils.encodeToString(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.role").value(DEFAULT_ROLE.toString()))
            .andExpect(jsonPath("$.posision").value(DEFAULT_POSISION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingUser() throws Exception {
        // Get the user
        restUserMockMvc.perform(get("/api/users/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

		int databaseSizeBeforeUpdate = userRepository.findAll().size();

        // Update the user
        user.setUsername(UPDATED_USERNAME);
        user.setPassword(UPDATED_PASSWORD);
        user.setEmail(UPDATED_EMAIL);
        user.setEdad(UPDATED_EDAD);
        user.setFechaNacimiento(UPDATED_FECHA_NACIMIENTO);
        user.setDescription(UPDATED_DESCRIPTION);
        user.setRole(UPDATED_ROLE);
        user.setPosision(UPDATED_POSISION);

        restUserMockMvc.perform(put("/api/users")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(user)))
                .andExpect(status().isOk());

        // Validate the User in the database
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(databaseSizeBeforeUpdate);
        User testUser = users.get(users.size() - 1);
        assertThat(testUser.getUsername()).isEqualTo(UPDATED_USERNAME);
        assertThat(testUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testUser.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testUser.getEdad()).isEqualTo(UPDATED_EDAD);
        assertThat(testUser.getFechaNacimiento().toDateTime(DateTimeZone.UTC)).isEqualTo(UPDATED_FECHA_NACIMIENTO);
        assertThat(testUser.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testUser.getRole()).isEqualTo(UPDATED_ROLE);
        assertThat(testUser.getPosision()).isEqualTo(UPDATED_POSISION);
    }

    @Test
    @Transactional
    public void deleteUser() throws Exception {
        // Initialize the database
        userRepository.saveAndFlush(user);

		int databaseSizeBeforeDelete = userRepository.findAll().size();

        // Get the user
        restUserMockMvc.perform(delete("/api/users/{id}", user.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<User> users = userRepository.findAll();
        assertThat(users).hasSize(databaseSizeBeforeDelete - 1);
    }
}
