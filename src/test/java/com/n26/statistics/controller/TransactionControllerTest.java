package com.n26.statistics.controller;

import com.n26.statistics.StatisticsServiceApplication;
import com.n26.statistics.domain.Statistics;
import com.n26.statistics.domain.Transaction;
import com.n26.statistics.service.TransactionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by egor on 03.07.17.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StatisticsServiceApplication.class)
@WebAppConfiguration
public class TransactionControllerTest {

    @Autowired
    TransactionService transactionService;
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.stream(converters)
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setUp() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        transactionService.clear();
    }

    @Test
    public void testAddTransactions() throws Exception {
        mockMvc.perform(post("/transactions")
                .content(this.json(new Transaction(1.0, System.currentTimeMillis())))
                .contentType(contentType))
                .andExpect(status().isCreated());
    }


    @Test
    public void testAddOldTransactions() throws Exception {
        mockMvc.perform(post("/transactions")
                .content(this.json(new Transaction(1.0, System.currentTimeMillis() - 61000)))
                .contentType(contentType))
                .andExpect(status().isNoContent());

    }


    @Test
    public void testGetEmptyStatistics() throws Exception {
        Statistics statistics = new Statistics();
        mockMvc.perform(get("/statistics"))
                .andExpect(content().json(json(statistics)));
    }


    @Test
    public void testGetAddedTransaction() throws Exception {
        Transaction transaction = new Transaction(1.0, System.currentTimeMillis());
        Statistics statistics = new Statistics(transaction);
        mockMvc.perform(post("/transactions")
                .content(this.json(transaction))
                .contentType(contentType))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/statistics"))
                .andExpect(content().json(json(statistics)));
    }


    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }
}