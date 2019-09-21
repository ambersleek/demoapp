package com.example.demo.aspect;

import com.example.demo.misc.MyMock;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Component
@Aspect
public class Mocky {

    // overall trying not to have to write multiple fetch methods as what I have done here.
    // fetchABCMockData and fetchDEFMockData
    // Trying to write this class once, make is as generic as possible, so that now and the next
    // time I need to mock another method, all I need to do is to give the annotation a new name
    // like 'XYZ', go to my mock file and and the relevant keys there and this class just works.
    // I don't know if this is even possible. Just trying to find the best way to perform this Aspect
    // mocking cos this is a mess now. We have like 8 different fetch methods, each detailing with different
    // method signatures, some having to hard code locale, some containing, params solely for the sake of mocking.

    private ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private Class returnType = null;

    public Mocky() {
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    }

    @Around("@annotation(myMock)")
    public Object getMockedData(ProceedingJoinPoint pjp, MyMock myMock) throws Throwable {

        MethodSignature signature = (MethodSignature) pjp.getSignature();
        returnType = signature.getReturnType();
        Object[] args = pjp.getArgs();
        String name = myMock.name();
        String response = null;
        List result = null;
        Map allData = getMockedDataFromFile(returnType);
        Map resultForKey = (Map) allData.get(myMock.name());

        if(resultForKey != null){
            result = (List) resultForKey.get("response");
        }

        switch (name){
            case "ABC":
                response = fetchABCMockData(result, (String) args[0]);
                break;
            case "DEF":
                // I need a way to specify locale so I can call fetchDEFMockData
                // I can't even do this. In actual case SomeRequest is another micro service.
                // I can't even cast args[0] to SomeRequest cos it shouldn't exists for me to cast here.
                // I would have to either create a brand new SomeRequest object here solely to cast and be able to do
                // someRequest.getLocale(). Or make that service which contains SomeRequest a dependency here
                // which would be circular dependency.
                break;
        }

        if(response != null){
            return mapper.readValue(response, returnType);
        }
        return pjp.proceed(args);
    }

    private String fetchDEFMockData(List result, Object arg) throws IOException {
        String locale = null;
        //in this case I need to call a locale specific file.
        // locale = (SomeRequest)arg; // I cant do this.
        // Made SomeRequest package private on purpose to simulate like it is in another micro service.

        // if I can get locale, then I could do following:
        Map m = getMockedDataFromFile(returnType, locale);

        // do some other work here like fetchABCMockData to get the data

        return null;
    }

    private String fetchABCMockData(List<Map> result, String name) {
        for(Map m : result){
            String key = m.keySet().toString().replaceAll("[\\[\\]]", "");
            if(name.equals(key)){
                return (String) m.get(key);
            }
        }
        return null;
    }

    //just to get an idea, only locale 'us' would give an example response here.
    //thus url https://demoaspect.free.beeceptor.com/allmocks/us
    private Map getMockedDataFromFile(Class returnType, String locale) throws IOException {
        String url = "https://demoaspect.free.beeceptor.com/allmocks/" + locale;
        String value = new RestTemplate().getForEntity(url, String.class).getBody();
        return mapper.readValue(value, Map.class);
    }

    private Map getMockedDataFromFile(Class returnType) throws IOException {
        String url = "https://demoaspect.free.beeceptor.com/allmocks";
        String value = new RestTemplate().getForEntity(url, String.class).getBody();
        return mapper.readValue(value, Map.class);
    }
}
