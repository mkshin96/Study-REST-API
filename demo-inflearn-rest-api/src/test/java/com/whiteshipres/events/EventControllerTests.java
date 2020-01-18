package com.whiteshipres.events;

import com.whiteshipres.accounts.Account;
import com.whiteshipres.accounts.AccountRepository;
import com.whiteshipres.accounts.AccountRole;
import com.whiteshipres.accounts.AccountService;
import com.whiteshipres.common.AppProperties;
import com.whiteshipres.common.BaseControllerTest;
import com.whiteshipres.common.TestDescription;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.common.util.Jackson2JsonParser;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
public class EventControllerTests extends BaseControllerTest {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AppProperties appProperties;

    @Before
    public void setUp(){
        this.eventRepository.deleteAll();
        this.accountRepository.deleteAll();
    }

    @Test
    @TestDescription("/api 요청 시")
    public void getIndex() throws Exception {
        mockMvc.perform(get("/api"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("index",
                        links(
                            linkWithRel("events").description("link to event"),
                            linkWithRel("profile").description("link to profile")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("response content type, it will be return hal json")
                        ),
                        responseFields(
                                fieldWithPath("_links.events.href").description("link to event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )

                ));

    }
    @Test
    public void createEvent() throws Exception{
        EventDto eventDto = EventDto.builder()
                    .name("Spring")
                    .description("Rest API Development with Spring")
                    .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 25, 14, 21))
                    .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 26, 14, 21))
                    .beginEventDateTime(LocalDateTime.of(2019, 9, 27, 14, 21))
                    .endEventDateTime(LocalDateTime.of(2019, 9, 28, 14, 21))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("강남역 d2 스타텁 팩토리")
                    .build();

        mockMvc.perform(post("/api/events")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(true))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .accept(MediaTypes.HAL_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_UTF8_VALUE))
                .andDo(document("create-event",
                            links(
                                    linkWithRel("self").description("link to self"),
                                    linkWithRel("query-events").description("link to query event"),
                                    linkWithRel("update-event").description("link to update event"),
                                    linkWithRel("profile").description("link to profile")
                            ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new evnet"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("새로 생성된 이벤트를 조회할 수 있는 url"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON타입이다.")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new evnet"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager").description("manager of event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query-events"),
                                fieldWithPath("_links.update-event.href").description("link to update event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }
    private String getBearerToken() throws Exception {
        return getBearerToken(true);
    }

    private String getBearerToken(Boolean needToCreateAccount) throws Exception {
        return "Bearer " + getAccessToken(needToCreateAccount);
    }

    private String getAccessToken(boolean needToCreateAccount) throws Exception{
        if (needToCreateAccount){
            createAccount();
        }

        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword() )
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jackson2JsonParser = new Jackson2JsonParser();
        return jackson2JsonParser.parseMap(responseBody).get("access_token").toString();
    }

    private Account createAccount() {
        //Given
        Account mugon = Account.builder().
                email(appProperties.getUserUsername())
                .password(appProperties.getUserPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        return this.accountService.saveAccount(mugon);
    }

    @Test
    public void createEvent_BadRequest() throws Exception{
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 25, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 26, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2019, 9, 27, 14, 21))
                .endEventDateTime(LocalDateTime.of(2019, 9, 28, 14, 21))
                .basePrice(100)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 d2 스타텁 팩토리")
                .free(true)
                .offline(false)
                .build();

        mockMvc.perform(post("/api/events/")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createEvent_Bad_Request_Empty_Input() throws Exception{
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(objectMapper.writeValueAsString(eventDto)))
                    .andExpect(status().isBadRequest());
    }

    @Test
    public void createEvent_Bad_Request_Wrong_Input() throws Exception{
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("Rest API Development with Spring")
                .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 25, 14, 21))
                .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 26, 14, 21))
                .beginEventDateTime(LocalDateTime.of(2019, 11, 24, 14, 21))
                .endEventDateTime(LocalDateTime.of(2019, 11, 23, 14, 21))
                .basePrice(10000)
                .maxPrice(200)
                .limitOfEnrollment(100)
                .location("강남역 d2 스타텁 팩토리")
                .build();

        this.mockMvc.perform(post("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(eventDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    public void testFree(){
        //given
        Event event = Event.builder().basePrice(0).maxPrice(0).build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isTrue();

        //given
        event = Event.builder().basePrice(100).maxPrice(100).build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isFalse();
    }

    @Test
    public void queryEvents() throws Exception {
        IntStream.range(0, 30).forEach(this::generatedEvent);

        this.mockMvc.perform(get("/api/events")
                .param("size", "10")
                .param("page", "1")
                .param("sort", "name,DESC"))
                .andDo(print())
                .andExpect(status().isOk());



    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEvent() throws Exception{
        //Given 이벤트 30개
        Account account = this.createAccount();

        IntStream.range(0, 30).forEach((i) -> this.generatedEvent(i, account));

        //when & then
        this.mockMvc.perform(get("/api/events")
                .param("page", "1")//0부터 시작
                .param("size", "10")
                .param("sort", "name,DESC")
                .accept(MediaTypes.HAL_JSON_VALUE)
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.profile.href").value("/docs/index.html#resources-events-list"))
                .andDo(document("query-events",
                        links(
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("prev").description("link to prev page"),
                                linkWithRel("self").description("link to self page"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("profile").description("link to details description")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON타입이다.")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[*].id").description("id of event"),
                                fieldWithPath("_embedded.eventList[*].name").description("name of event"),
                                fieldWithPath("_embedded.eventList[*].description").description("description of event"),
                                fieldWithPath("_embedded.eventList[*].beginEnrollmentDateTime").description("beginEnrollmentDateTime of event"),
                                fieldWithPath("_embedded.eventList[*].closeEnrollmentDateTime").description("loseEnrollmentDateTime of event"),
                                fieldWithPath("_embedded.eventList[*].beginEventDateTime").description("beginEventDateTime of event"),
                                fieldWithPath("_embedded.eventList[*].endEventDateTime").description("endEventDateTime of event"),
                                fieldWithPath("_embedded.eventList[*].location").description("location of event"),
                                fieldWithPath("_embedded.eventList[*].basePrice").description("basePrice of event"),
                                fieldWithPath("_embedded.eventList[*].maxPrice").description("maxPrice of event"),
                                fieldWithPath("_embedded.eventList[*].limitOfEnrollment").description("limitOfEnrollment of event"),
                                fieldWithPath("_embedded.eventList[*].offline").description("offline of event"),
                                fieldWithPath("_embedded.eventList[*].free").description("free of event"),
                                fieldWithPath("_embedded.eventList[*].eventStatus").description("eventStatus of event"),
                                fieldWithPath("_embedded.eventList[*].manager.id").description("manager id of event"),
                                fieldWithPath("_embedded.eventList[*]._links.self.href").description("link to self"),
                                fieldWithPath("_links.first.href").description("link to first"),
                                fieldWithPath("_links.prev.href").description("link to prev"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.next.href").description("link to next"),
                                fieldWithPath("_links.last.href").description("link to last"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("page.size").description("page size"),
                                fieldWithPath("page.totalElements").description("totalElements of page"),
                                fieldWithPath("page.totalPages").description("totalPages of page"),
                                fieldWithPath("page.number").description("number of page")
                         ),
                        requestParameters(
                            parameterWithName("page").description("page of event lists"),
                                parameterWithName("size").description("size of event list"),
                                parameterWithName("sort").description("sort of event list")
                        )

                ))

        ;
    }

    @Test
    @TestDescription("인증정보를 포함해 30개의 이벤트를 10개씩 두번째 페이지 조회하기")
    public void queryEventWithAuthentication() throws Exception{
        //Given 이벤트 30개
        Account account = this.createAccount();
        IntStream.range(0, 30).forEach((i) -> this.generatedEvent(i, account));

        //when & then
        this.mockMvc.perform(get("/api/events")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .param("page", "1")//0부터 시작
                .param("size", "10")
                .param("sort", "name,DESC")
                .accept(MediaTypes.HAL_JSON_VALUE)
        )

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("page").exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andExpect(jsonPath("_links.create-event").exists())
                .andExpect(jsonPath("_links.profile.href").value("/docs/index.html#authorized-resources-events-list"))
                .andDo(document("authorized-query-events",
                        links(
                                linkWithRel("first").description("link to first page"),
                                linkWithRel("prev").description("link to prev page"),
                                linkWithRel("self").description("link to self page"),
                                linkWithRel("next").description("link to next page"),
                                linkWithRel("last").description("link to last page"),
                                linkWithRel("profile").description("link to details description"),
                                linkWithRel("create-event").description("link to create-event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept header"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("need oauth token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("HAL JSON타입이다.")
                        ),
                        responseFields(
                                fieldWithPath("_embedded.eventList[*].id").description("id of event"),
                                fieldWithPath("_embedded.eventList[*].name").description("name of event"),
                                fieldWithPath("_embedded.eventList[*].description").description("description of event"),
                                fieldWithPath("_embedded.eventList[*].beginEnrollmentDateTime").description("beginEnrollmentDateTime of event"),
                                fieldWithPath("_embedded.eventList[*].closeEnrollmentDateTime").description("loseEnrollmentDateTime of event"),
                                fieldWithPath("_embedded.eventList[*].beginEventDateTime").description("beginEventDateTime of event"),
                                fieldWithPath("_embedded.eventList[*].endEventDateTime").description("endEventDateTime of event"),
                                fieldWithPath("_embedded.eventList[*].location").description("location of event"),
                                fieldWithPath("_embedded.eventList[*].basePrice").description("basePrice of event"),
                                fieldWithPath("_embedded.eventList[*].maxPrice").description("maxPrice of event"),
                                fieldWithPath("_embedded.eventList[*].limitOfEnrollment").description("limitOfEnrollment of event"),
                                fieldWithPath("_embedded.eventList[*].offline").description("offline of event"),
                                fieldWithPath("_embedded.eventList[*].free").description("free of event"),
                                fieldWithPath("_embedded.eventList[*].eventStatus").description("eventStatus of event"),
                                fieldWithPath("_embedded.eventList[*].manager.id").description("manager id of event"),
                                fieldWithPath("_embedded.eventList[*]._links.self.href").description("link to self"),
                                fieldWithPath("_links.first.href").description("link to first"),
                                fieldWithPath("_links.prev.href").description("link to prev"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.next.href").description("link to next"),
                                fieldWithPath("_links.last.href").description("link to last"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.create-event.href").description("link to create-event"),
                                fieldWithPath("page.size").description("page size"),
                                fieldWithPath("page.totalElements").description("totalElements of page"),
                                fieldWithPath("page.totalPages").description("totalPages of page"),
                                fieldWithPath("page.number").description("number of page")
                        ),
                        requestParameters(
                                parameterWithName("page").description("page of event lists"),
                                parameterWithName("size").description("size of event list"),
                                parameterWithName("sort").description("sort of event list")
                        )
                ))

        ;
    }

    @Test
    @TestDescription("인증되지 않은 사용자가 기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception{
        Account account = this.createAccount();

        //Given
        Event event = this.generatedEvent(100, account);

        //when & then
        this.mockMvc.perform(get("/api/events/{id}", event.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
        .andDo(document("get-an-event",
                links(
                        linkWithRel("self").description("link to self"),
                        linkWithRel("profile").description("link to profile"),
                        linkWithRel("query-events").description("link to query-events")
                        ),
                responseHeaders(
                        headerWithName(HttpHeaders.CONTENT_TYPE).description("It is content type, It will be return hal json")
                ),
                responseFields(
                        fieldWithPath("id").description("id of new event"),
                        fieldWithPath("name").description("name of new event"),
                        fieldWithPath("description").description("description of new event"),
                        fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                        fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                        fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                        fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                        fieldWithPath("location").description("location of new event"),
                        fieldWithPath("basePrice").description("basePrice of new evnet"),
                        fieldWithPath("maxPrice").description("maxPrice of new event"),
                        fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                        fieldWithPath("free").description("it tells if this event is free or not"),
                        fieldWithPath("offline").description("it tells if this event is offline or not"),
                        fieldWithPath("eventStatus").description("event status"),
                        fieldWithPath("manager.id").description("manager id of event"),
                        fieldWithPath("_links.self.href").description("link to self"),
                        fieldWithPath("_links.profile.href").description("link to profile"),
                        fieldWithPath("_links.query-events.href").description("link to query-events")
                )
        ))
        ;
    }

    @Test
    @TestDescription("인증된 사용자가 다른 사용자의 게시물을 조회하는 경우")
    public void getEventOtherPerson() throws Exception{
        
        Account account = this.createAccount();
        
        //다른 사용자 생성
        Account otherPerson = Account.builder().
                email(appProperties.getAdminUsername())
                .password(appProperties.getAdminPassword())
                .roles(Set.of(AccountRole.ADMIN, AccountRole.USER))
                .build();
        
        this.accountService.saveAccount(otherPerson);

        ResultActions resultActions = this.mockMvc.perform(post("/oauth/token")
                .with(httpBasic(appProperties.getClientId(), appProperties.getClientSecret()))
                .param("username", appProperties.getUserUsername())
                .param("password", appProperties.getUserPassword() )
                .param("grant_type", "password"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("access_token").exists());

        String responseBody = resultActions.andReturn().getResponse().getContentAsString();
        Jackson2JsonParser jackson2JsonParser = new Jackson2JsonParser();
        String other_people_access_token = jackson2JsonParser.parseMap(responseBody).get("access_token").toString();

        //Given
        Event event = this.generatedEvent(100, account);

        //when & then
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, other_people_access_token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("get-an-event-other-person",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("query-events").description("link to query-events")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("need oauth token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("It is content type, It will be return hal json")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new evnet"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager.id").description("manager id of event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.query-events.href").description("link to query-events")
                        )

                ))
        ;
    }
    
    @Test
    @TestDescription("인증된 사용자가 자신의 이벤트를 하나 조회하기")
    public void getEventWithAuthentication() throws Exception{
        Account account = this.createAccount();

        //Given
        Event event = this.generatedEvent(100, account);

        //when & then
        this.mockMvc.perform(get("/api/events/{id}", event.getId())
        .header(HttpHeaders.AUTHORIZATION, getBearerToken(false)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").exists())
                .andExpect(jsonPath("name").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("authorized-get-an-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("query-events").description("link to query-events"),
                                linkWithRel("update-event").description("link to update-event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("need oauth token")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("It is content type, It will be return hal json")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new evnet"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager.id").description("manager id of event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.query-events.href").description("link to query-events"),
                                fieldWithPath("_links.update-event.href").description("link to update-event")
                        )

                ))
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을 때 404 응답받기")
    public void getEvent404() throws Exception {
        this.mockMvc.perform(get("/api/events/11883"))
                .andExpect(status().isNotFound())

        ;
    }

    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent() throws Exception {
        //Given
        Account account = this.createAccount();

        Event event = generatedEvent(50, account);
        EventDto eventDto = modelMapper.map(event, EventDto.class);
        String eventName = "Updated Event";
        eventDto.setName(eventName);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(false))
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(eventName))
                .andExpect(jsonPath("_links.self").exists())
                .andDo(document("update-event",
                        links(
                                linkWithRel("self").description("link to self"),
                                linkWithRel("profile").description("link to profile"),
                                linkWithRel("query-events").description("link to event list")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type"),
                                headerWithName(HttpHeaders.AUTHORIZATION).description("Authorization")
                        ),
                        requestFields(
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new evnet"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type of response")
                        ),
                        responseFields(
                                fieldWithPath("id").description("id of new event"),
                                fieldWithPath("name").description("name of new event"),
                                fieldWithPath("description").description("description of new event"),
                                fieldWithPath("beginEnrollmentDateTime").description("beginEnrollmentDateTime of new event"),
                                fieldWithPath("closeEnrollmentDateTime").description("closeEnrollmentDateTime of new event"),
                                fieldWithPath("beginEventDateTime").description("beginEventDateTime of new event"),
                                fieldWithPath("endEventDateTime").description("endEventDateTime of new event"),
                                fieldWithPath("location").description("location of new event"),
                                fieldWithPath("basePrice").description("basePrice of new evnet"),
                                fieldWithPath("maxPrice").description("maxPrice of new event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment of new event"),
                                fieldWithPath("free").description("it tells if this event is free or not"),
                                fieldWithPath("offline").description("it tells if this event is offline or not"),
                                fieldWithPath("eventStatus").description("event status"),
                                fieldWithPath("manager.id").description("manager id of event"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.profile.href").description("link to profile"),
                                fieldWithPath("_links.query-events.href").description("link to event list")
                        )

                ))

        ;
    }

    @Test
    @TestDescription("입력값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty_Input() throws Exception {
        //Given
        Event event = generatedEvent(50);
        EventDto eventDto = new EventDto();

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong_Input() throws Exception {
        //Given
        Event event = generatedEvent(50);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        //When & Then
        this.mockMvc.perform(put("/api/events/{id}", event.getId())
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404() throws Exception {
        //Given
        Event event = generatedEvent(50);
        EventDto eventDto = this.modelMapper.map(event, EventDto.class);

        //When & Then
        this.mockMvc.perform(put("/api/events/11330")
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }

    private Event generatedEvent(int i, Account account) {
        Event event = buildEvent(i);
        event.setManager(account);
        return this.eventRepository.save(event);
    }

    private Event generatedEvent(int i) {
        Event event = buildEvent(i);
        return this.eventRepository.save(event);
    }

    private Event buildEvent(int i) {
        return Event.builder()
                    .name("event " + i)
                    .description("Rest API Development with Spring")
                    .beginEnrollmentDateTime(LocalDateTime.of(2019, 9, 25, 14, 21))
                    .closeEnrollmentDateTime(LocalDateTime.of(2019, 9, 26, 14, 21))
                    .beginEventDateTime(LocalDateTime.of(2019, 9, 27, 14, 21))
                    .endEventDateTime(LocalDateTime.of(2019, 9, 28, 14, 21))
                    .basePrice(100)
                    .maxPrice(200)
                    .limitOfEnrollment(100)
                    .location("강남역 d2 스타텁 팩토리")
                    .free(false)
                    .offline(true)
                    .eventStatus(EventStatus.DRAFT)
                    .build();
    }


}