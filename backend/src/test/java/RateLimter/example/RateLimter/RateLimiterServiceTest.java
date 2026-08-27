package RateLimter.example.RateLimter;


import RateLimter.example.RateLimter.services.RateLimitResult;
import RateLimter.example.RateLimter.services.RateLimiterService;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.CountDownLatch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
@SpringBootTest
@AutoConfigureMockMvc
class RateLimiterServiceTest {
    @Autowired
    RateLimiterService rateLimiterService;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAllowHttpRequest() throws Exception {

        mockMvc.perform(get("/hello"))
                .andExpect(status().isOk());
    }

    @Test
    void cheackIfTheUserAsTheToken() {
        String test = "test-user" + System.currentTimeMillis();
        RateLimitResult result = rateLimiterService.access(test);
        assertTrue(result.isResult());

    }

    @Test
    void cheackForFalse() {
        String test = "test-limit-user" + System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            rateLimiterService.access(test);
        }
        RateLimitResult reault = rateLimiterService.access(test);
        assertFalse(reault.isResult());
    }

    @Test
    void cheackTheRefill() throws InterruptedException {
        String test = "test-refil-user" + System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            rateLimiterService.access(test);
        }
        RateLimitResult result = rateLimiterService.access(test);
        assertFalse(result.isResult());
        Thread.sleep(1000);
        result = rateLimiterService.access(test);
        assertTrue(result.isResult());
    }

    @Test
    void shouldReturn429AfterLimitIsReached() throws Exception {

        String testIp = "192.168.1.100";

        for (int i = 0; i < 5; i++) {

            mockMvc.perform(
                    get("/hello")
                            .with(request -> {
                                request.setRemoteAddr(testIp);
                                return request;
                            })
            ).andExpect(status().isOk());
        }

        mockMvc.perform(
                get("/hello")
                        .with(request -> {
                            request.setRemoteAddr(testIp);
                            return request;
                        })
        ).andExpect(status().isTooManyRequests());
    }

    @Test
    void shouldReturnRetryAfterHeaderWhenLimitIsReached() throws Exception {

        String testIp = "192.168.1.102";

        for (int i = 0; i < 5; i++) {

            mockMvc.perform(
                    get("/hello")
                            .with(request -> {
                                request.setRemoteAddr(testIp);
                                return request;
                            })
            );
        }

        mockMvc.perform(
                        get("/hello")
                                .with(request -> {
                                    request.setRemoteAddr(testIp);
                                    return request;
                                })
                )
                .andExpect(status().isTooManyRequests())
                .andExpect(header().exists("Retry-After"));
    }
    @Test
    void shouldNotAllowMoreThanCapacityUnderConcurrency()
            throws InterruptedException, ExecutionException {

        String userId =
                "concurrent-test-" + System.currentTimeMillis();

        int totalRequests = 20;

        ExecutorService executor =
                Executors.newFixedThreadPool(totalRequests);

        CountDownLatch startLatch =
                new CountDownLatch(1);

        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < totalRequests; i++) {

            tasks.add(() -> {

                startLatch.await();

                return rateLimiterService
                        .access(userId)
                        .isResult();
            });
        }

        List<Future<Boolean>> results =
                tasks.stream()
                        .map(executor::submit)
                        .toList();

        startLatch.countDown();

        long allowedRequests = 0;

        for (Future<Boolean> result : results) {

            if (result.get()) {
                allowedRequests++;
            }
        }

        executor.shutdown();

        assertEquals(5, allowedRequests);
    }
}