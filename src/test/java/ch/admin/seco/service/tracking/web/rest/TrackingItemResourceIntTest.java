package ch.admin.seco.service.tracking.web.rest;

import static ch.admin.seco.service.tracking.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import ch.admin.seco.service.tracking.TrackingApp;
import ch.admin.seco.service.tracking.domain.TrackingItem;
import ch.admin.seco.service.tracking.repository.TrackingItemRepository;
import ch.admin.seco.service.tracking.service.TrackingItemService;
import ch.admin.seco.service.tracking.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the TrackingItemResource REST controller.
 *
 * @see TrackingItemResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TrackingApp.class)
public class TrackingItemResourceIntTest {

    private static final String DEFAULT_EVENT = "AAAAAAAAAA";

    private static final Map<String, String> DEFAULT_DATA = Collections.singletonMap("externalId", "externalIdValue");

    private static final Instant DEFAULT_TIME_STAMP = Instant.ofEpochMilli(0L);

    private static final String DEFAULT_TRACKING_ID = "AAAAAAAAAA";

    private static final String DEFAULT_IP_HASH = "AAAAAAAAAA";

    private static final String DEFAULT_LOCALE = "AAAAAAAAAA";

    @Autowired
    private TrackingItemRepository trackingItemRepository;

    @Autowired
    private TrackingItemService trackingItemService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTrackingItemMockMvc;

    private TrackingItem trackingItem;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final TrackingItemResource trackingItemResource = new TrackingItemResource(trackingItemService);
        this.restTrackingItemMockMvc = MockMvcBuilders.standaloneSetup(trackingItemResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static TrackingItem createEntity(EntityManager em) {
        TrackingItem trackingItem = new TrackingItem()
            .event(DEFAULT_EVENT)
            .data(new JSONObject(DEFAULT_DATA).toString())
            .timeStamp(DEFAULT_TIME_STAMP)
            .trackingId(DEFAULT_TRACKING_ID)
            .ipHash(DEFAULT_IP_HASH)
            .locale(DEFAULT_LOCALE);
        return trackingItem;
    }

    @Before
    public void initTest() {
        trackingItem = createEntity(em);
    }

    @Test
    @Transactional
    public void createTrackingItem() throws Exception {
        int databaseSizeBeforeCreate = trackingItemRepository.findAll().size();

        TrackingItem trackingItem = new TrackingItem()
            .event(DEFAULT_EVENT)
            .data(new JSONObject(DEFAULT_DATA).toString())
            .trackingId(DEFAULT_TRACKING_ID)
            .locale(DEFAULT_LOCALE);

        // Create the TrackingItem
        restTrackingItemMockMvc.perform(post("/api/tracking-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trackingItem)))
            .andExpect(status().isOk());

        // Validate the TrackingItem in the database
        List<TrackingItem> trackingItemList = trackingItemRepository.findAll();
        assertThat(trackingItemList).hasSize(databaseSizeBeforeCreate + 1);
        TrackingItem testTrackingItem = trackingItemList.get(trackingItemList.size() - 1);
        assertThat(testTrackingItem.getEvent()).isEqualTo(DEFAULT_EVENT);
        assertThat(testTrackingItem.getData()).isEqualTo(new JSONObject(DEFAULT_DATA).toString());
        assertThat(testTrackingItem.getTimeStamp()).isBetween(Instant.now().minus(Duration.ofMinutes(1)), Instant.now().plus(Duration.ofMinutes(1)));
        assertThat(testTrackingItem.getTrackingId()).isEqualTo(DEFAULT_TRACKING_ID);
        assertThat(testTrackingItem.getLocale()).isEqualTo(DEFAULT_LOCALE);
    }

    @Test
    @Transactional
    public void createTrackingItemWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = trackingItemRepository.findAll().size();

        // Create the TrackingItem with an existing ID
        trackingItem.setId(UUID.randomUUID());

        // An entity with an existing ID cannot be created, so this API call must fail
        restTrackingItemMockMvc.perform(post("/api/tracking-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trackingItem)))
            .andExpect(status().isBadRequest());

        // Validate the TrackingItem in the database
        List<TrackingItem> trackingItemList = trackingItemRepository.findAll();
        assertThat(trackingItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkEventIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingItemRepository.findAll().size();
        // set the field null
        trackingItem.setEvent(null);

        // Create the TrackingItem, which fails.

        restTrackingItemMockMvc.perform(post("/api/tracking-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trackingItem)))
            .andExpect(status().isBadRequest());

        List<TrackingItem> trackingItemList = trackingItemRepository.findAll();
        assertThat(trackingItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTrackingIdIsRequired() throws Exception {
        int databaseSizeBeforeTest = trackingItemRepository.findAll().size();
        // set the field null
        trackingItem.setTrackingId(null);

        // Create the TrackingItem, which fails.

        restTrackingItemMockMvc.perform(post("/api/tracking-items")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(trackingItem)))
            .andExpect(status().isBadRequest());

        List<TrackingItem> trackingItemList = trackingItemRepository.findAll();
        assertThat(trackingItemList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTrackingItems() throws Exception {
        // Initialize the database
        trackingItemRepository.saveAndFlush(trackingItem);

        // Get all the trackingItemList
        restTrackingItemMockMvc.perform(get("/api/tracking-items?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(trackingItem.getId().toString())))
            .andExpect(jsonPath("$.[*].event").value(hasItem(DEFAULT_EVENT.toString())))
            .andExpect(jsonPath("$.[*].data.externalId").value(hasItem("externalIdValue")))
            .andExpect(jsonPath("$.[*].timeStamp").value(hasItem(DEFAULT_TIME_STAMP.toString())))
            .andExpect(jsonPath("$.[*].trackingId").value(hasItem(DEFAULT_TRACKING_ID.toString())))
            .andExpect(jsonPath("$.[*].ipHash").value(hasItem(DEFAULT_IP_HASH.toString())))
            .andExpect(jsonPath("$.[*].locale").value(hasItem(DEFAULT_LOCALE.toString())));
    }

    @Test
    @Transactional
    public void getTrackingItem() throws Exception {
        // Initialize the database
        trackingItemRepository.saveAndFlush(trackingItem);

        // Get the trackingItem
        restTrackingItemMockMvc.perform(get("/api/tracking-items/{id}", trackingItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(trackingItem.getId().toString()))
            .andExpect(jsonPath("$.event").value(DEFAULT_EVENT.toString()))
            .andExpect(jsonPath("$.data.externalId").value("externalIdValue"))
            .andExpect(jsonPath("$.timeStamp").value(DEFAULT_TIME_STAMP.toString()))
            .andExpect(jsonPath("$.trackingId").value(DEFAULT_TRACKING_ID.toString()))
            .andExpect(jsonPath("$.ipHash").value(DEFAULT_IP_HASH.toString()))
            .andExpect(jsonPath("$.locale").value(DEFAULT_LOCALE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingTrackingItem() throws Exception {
        // Get the trackingItem
        restTrackingItemMockMvc.perform(get("/api/tracking-items/{id}", UUID.randomUUID()))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void deleteTrackingItem() throws Exception {
        // Initialize the database
        trackingItemService.save(trackingItem);

        int databaseSizeBeforeDelete = trackingItemRepository.findAll().size();

        // Get the trackingItem
        restTrackingItemMockMvc.perform(delete("/api/tracking-items/{id}", trackingItem.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TrackingItem> trackingItemList = trackingItemRepository.findAll();
        assertThat(trackingItemList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TrackingItem.class);
        TrackingItem trackingItem1 = new TrackingItem();
        trackingItem1.setId(UUID.randomUUID());
        TrackingItem trackingItem2 = new TrackingItem();
        trackingItem2.setId(trackingItem1.getId());
        assertThat(trackingItem1).isEqualTo(trackingItem2);
        trackingItem2.setId(UUID.randomUUID());
        assertThat(trackingItem1).isNotEqualTo(trackingItem2);
        trackingItem1.setId(null);
        assertThat(trackingItem1).isNotEqualTo(trackingItem2);
    }
}
