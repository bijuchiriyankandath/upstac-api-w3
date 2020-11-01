package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.server.ResponseStatusException;
import org.upgrad.upstac.config.security.UserLoggedInService;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.TestRequest;
import org.upgrad.upstac.testrequests.consultation.Consultation;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.consultation.DoctorSuggestion;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.testrequests.RequestStatus;
import org.upgrad.upstac.testrequests.TestRequestQueryService;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.UserService;
import org.upgrad.upstac.users.models.AccountStatus;
import org.upgrad.upstac.users.models.Gender;
import org.upgrad.upstac.users.roles.Role;
import org.upgrad.upstac.users.roles.RoleRepository;
import org.upgrad.upstac.users.roles.RoleService;
import org.upgrad.upstac.users.roles.UserRole;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConsultationControllerTest {

    @InjectMocks
    ConsultationController consultationController;

    @Mock
   TestRequestUpdateService testRequestUpdateService;

    @Mock
    UserLoggedInService userLoggedInService;

    @Test
    public void calling_assignForConsulation_with_ValidTestRequest_should_return_valid_TestRequestResponse(){

        //Assign
        CreateTestRequest newTestRequest = mockTestRequest();
        User user = createMockDoctor();
        //System.out.println(user.getId());
        TestRequest mockedResponse = mockTesRequestResponse(newTestRequest);
        //System.out.println(mockedResponse.getName());
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.assignForConsultation(123L,user)).thenReturn(mockedResponse);

        //Act
        TestRequest testRequest = consultationController.assignForConsultation(123L);

        //Assert
        assertNotNull(testRequest,"Response received is null");
        assertEquals(mockedResponse,testRequest);
    }

    @Test
    public void calling_assignForConsultation_with_InvalidTestRequest_should_return_AppExeception(){

        //Assert
        User mockDoctor = createMockDoctor();
        CreateTestRequest newTestResquest = mockTestRequest();
        TestRequest mockResponse = mockTesRequestResponse(newTestResquest);

        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(mockDoctor);
        Mockito.when(testRequestUpdateService.assignForConsultation(-123L,mockDoctor)).thenThrow(new AppException("Invalid Data"));

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()-> {
            consultationController.assignForConsultation(-123l);
        });

        //Assert
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid Data",exception.getReason());
    }


    public User createMockDoctor(){
        User mockDoctor = new User();
        mockDoctor.setId(102L);
        mockDoctor.setLastName("Doctor");
        mockDoctor.setLastName("Name");
        mockDoctor.setUserName("userName");
        return mockDoctor;
    }

    public CreateTestRequest mockTestRequest(){
        CreateTestRequest newTestRequest = new CreateTestRequest();
        newTestRequest.setName("requestUserName");
        newTestRequest.setAddress("some Address");
        newTestRequest.setAge(45);
        newTestRequest.setEmail("userName@upgrad.com");
        newTestRequest.setPhoneNumber("9823420232");
        newTestRequest.setGender(Gender.FEMALE);
        newTestRequest.setPinCode(823923);
        return newTestRequest;
    }

    public TestRequest mockTesRequestResponse(CreateTestRequest createTestRequest){
        TestRequest mockedTestRequest = new TestRequest();
        mockedTestRequest.setRequestId(101L);
        mockedTestRequest.setName(createTestRequest.getName());
        mockedTestRequest.setGender(createTestRequest.getGender());
        mockedTestRequest.setAge(createTestRequest.getAge());
        mockedTestRequest.setEmail(createTestRequest.getEmail());
        mockedTestRequest.setPhoneNumber(createTestRequest.getPhoneNumber());
        mockedTestRequest.setAddress(createTestRequest.getAddress());
        mockedTestRequest.setPinCode(createTestRequest.getPinCode());
        mockedTestRequest.setStatus(RequestStatus.LAB_TEST_COMPLETED);

        return mockedTestRequest;
    }




    /*

    @SpringBootTest
    @Slf4j

    @Autowired
    ConsultationController consultationController;


    @Autowired
    TestRequestQueryService testRequestQueryService;


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_update_the_request_status() {

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_COMPLETED);

        //Implement this method

        //Create another object of the TestRequest method and explicitly assign this object for Consultation using assignForConsultation() method
        // from consultationController class. Pass the request id of testRequest object.

        //Act
        TestRequest newTestRequest = consultationController.assignForConsultation(testRequest.getRequestId());

        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'DIAGNOSIS_IN_PROCESS'
        // make use of assertNotNull() method to make sure that the consultation value of second object is not null
        // use getConsultation() method to get the lab result

        //Assert

        assertThat("Request IDs are not same on the 2 test request objects: " + testRequest.getRequestId() + " " + newTestRequest.getRequestId(),
                testRequest.getRequestId().equals(newTestRequest.getRequestId()));
        assertThat("Status of second test request object is not DIAGNOSIS_IN_PROCESS: " + newTestRequest.getStatus(),
                RequestStatus.DIAGNOSIS_IN_PROCESS.toString().equals(newTestRequest.getStatus()));
        assertNotNull( newTestRequest.getConsultation().getSuggestion());
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        return testRequestQueryService.findBy(status).stream().findFirst().get();
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_assignForConsultation_with_valid_test_request_id_should_throw_exception() {

        //Arrange
        Long InvalidRequestId = -34L;

        //Implement this method


        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForConsultation() method
        // of consultationController with InvalidRequestId as Id

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> {
                    TestRequest newTestRequest = consultationController.assignForConsultation(InvalidRequestId);
                });


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

        //Assert
        assertThat("Exception Message Doesn't contain text Invalid ID: " + exception.getMessage(),exception.getMessage(),containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_valid_test_request_id_should_update_the_request_status_and_update_consultation_details() {

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        //Implement this method
        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter

        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);

        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'COMPLETED'. Make use of updateConsultation() method from labRequestController class (Pass the previously created two objects as parameters)
        TestRequest newTestRequest = new TestRequest();

        //Act
        newTestRequest.setStatus(consultationController.updateConsultation(testRequest.getRequestId(),createConsultationRequest).getStatus());

        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'COMPLETED'
        // 3. the suggestion of both the objects created should be same. Make use of getSuggestion() method to get the results.

        //Assert
        assertThat("Request ids on the 2 test request objects are not same: " + testRequest.getRequestId() + " " + newTestRequest.getRequestId(),
                testRequest.getRequestId().equals(newTestRequest.getRequestId()));
        assertThat("Status on second test request object is not COMPLETED: " + testRequest.getStatus(),
                RequestStatus.COMPLETED.toString().equals(newTestRequest.getStatus().toString()));
        assertThat("Suggestions on the 2 test request objects are not same: " + testRequest.getConsultation().getSuggestion() + " " + newTestRequest.getConsultation().getSuggestion(),
                testRequest.getConsultation().getSuggestion().toString().equals(newTestRequest.getConsultation().getSuggestion().toString()));
    }


    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_test_request_id_should_throw_exception() {

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        //Implement this method

        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> {
            consultationController.updateConsultation(-123L,createConsultationRequest);
        });


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

        //Assert
        assertThat("Exception message doesn't contain string Invalid ID: ",exception.getMessage(),containsString("Invalid ID"));
    }

    @Test
    @WithUserDetails(value = "doctor")
    public void calling_updateConsultation_with_invalid_empty_status_should_throw_exception() {

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.DIAGNOSIS_IN_PROCESS);

        //Implement this method

        //Create an object of CreateConsultationRequest and call getCreateConsultationRequest() to create the object. Pass the above created object as the parameter
        // Set the suggestion of the above created object to null.
        CreateConsultationRequest createConsultationRequest = getCreateConsultationRequest(testRequest);
        createConsultationRequest.setSuggestion(null);

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateConsultation() method
        // of consultationController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,()-> {
            consultationController.updateConsultation(-123L,createConsultationRequest);
        });

        //Assert
        assertThat("Exception message doesn't contain string Invalid ID: ",exception.getMessage(),containsString("ConstraintViolationException"));
    }

    public CreateConsultationRequest getCreateConsultationRequest(TestRequest testRequest) {

        //Create an object of CreateLabResult and set all the values
        // if the lab result test status is Positive, set the doctor suggestion as "HOME_QUARANTINE" and comments accordingly
        // else if the lab result status is Negative, set the doctor suggestion as "NO_ISSUES" and comments as "Ok"
        // Return the object
        CreateLabResult newLabResult = new CreateLabResult();
        newLabResult.setBloodPressure(testRequest.getLabResult().getBloodPressure());
        newLabResult.setTemperature(testRequest.getLabResult().getTemperature());
        newLabResult.setOxygenLevel(testRequest.getLabResult().getOxygenLevel());
        newLabResult.setHeartBeat(testRequest.getLabResult().getHeartBeat());
        newLabResult.setResult(testRequest.getLabResult().getResult());

        CreateConsultationRequest consultationRequest = new CreateConsultationRequest();

        if (testRequest.getLabResult().getResult().equals(TestStatus.POSITIVE)) {
            consultationRequest.setSuggestion(DoctorSuggestion.HOME_QUARANTINE);
            consultationRequest.setComments("Stay at home for 14 days");
        } else {
            consultationRequest.setSuggestion(DoctorSuggestion.NO_ISSUES);
            consultationRequest.setComments("Ok");
        }

        return consultationRequest; // Replace this line with your code

    }*/

}