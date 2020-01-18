package com.whiteshipres.events;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(JUnitParamsRunner.class)
public class EventTest {

    @Test
    public void builder(){
        Event event = Event.builder()
                .name("인프런 스프링 REST API")
                .description("REST API development with spring")
                .build();
        assertThat(event).isNotNull();
    }

    //자바 빈 스팩
    @Test
    public void javaBean(){
        //given
        String name = "이벤트";
        String spring = "스프링";

        //when
        Event event = new Event();
        event.setName(name);
        event.setDescription(spring);

        //then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(spring);

    }

    @Test
    @Parameters(method = "parametersForTestFree, parametersForTestFree2")
    public void testFree(int basePrice, int maxPrice, boolean isFree){
        //given
        Event event = Event.builder().basePrice(basePrice).maxPrice(maxPrice).build();

        //when
        event.update();

        //then
        assertThat(event.isFree()).isEqualTo(isFree);
    }

    private Object[] parametersForTestFree(){
        return new Object[]{
                new Object[] {0, 0, true},
                new Object[] {100, 0, false},
                new Object[] {0, 100, false},
                new Object[] {100, 200, false}
        };
    }

    private Object[] parametersForTestFree2(){
        return new Object[]{
                new Object[] {100, 500, false},
                new Object[] {100, 0, false},
                new Object[] {0, 0, true},
                new Object[] {100, 200, false}
        };
    }

    @Test
    @Parameters
    public void testOffLine(String location, boolean isOffline){
        //given
        Event event = Event.builder().location(location).build();

        //when
        event.update();

        //then
        assertThat(event.isOffline()).isEqualTo(isOffline);
    }

    private Object[] parametersForTestOffLine(){
        return new Object[] {
            new Object[] {"강남역 d2 스타트업 팩토리", true},
            new Object[] {"", false},
            new Object[] {null, false},
            new Object[] {"대신동 사거리", true},
            new Object[] {"        ", false}

        };
    }
}