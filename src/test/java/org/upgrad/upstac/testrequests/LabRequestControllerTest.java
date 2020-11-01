package org.upgrad.upstac.testrequests;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
import org.upgrad.upstac.exception.UpgradResponseStatusException;
import org.upgrad.upstac.testrequests.consultation.ConsultationController;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.testrequests.lab.LabRequestController;
import org.upgrad.upstac.testrequests.lab.TestStatus;
import org.upgrad.upstac.users.User;
import org.upgrad.upstac.users.models.Gender;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


//@SpringBootTest
//@Slf4j
@ExtendWith(MockitoExtension.class)
class LabRequestControllerTest {

    @InjectMocks
    LabRequestController labRequestController;

    @Mock
    TestRequestUpdateService testRequestUpdateService;

    @Mock
    UserLoggedInService userLoggedInService;

    @Test
    public void calling_assignForLabTest_with_ValidTestRequest_should_return_valid_TestRequestResponse(){

        //Assign
        CreateTestRequest newTestRequest = mockTestRequest();
        User user = createMockTester();
        //System.out.println(user.getId());
        TestRequest mockedResponse = mockTesRequestResponse(newTestRequest);
        //System.out.println(mockedResponse.getName());
        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(user);
        Mockito.when(testRequestUpdateService.assignForLabTest(123L,user)).thenReturn(mockedResponse);

        //Act
        TestRequest testRequest = labRequestController.assignForLabTest(123L);

        //Assert
        assertNotNull(testRequest,"Response received is null");
        assertEquals(mockedResponse,testRequest);
    }

    @Test
    public void calling_assignForLabTest_with_InvalidTestRequest_should_return_AppExeception(){

        //Assert
        User mockDoctor = createMockTester();
        CreateTestRequest newTestResquest = mockTestRequest();
        TestRequest mockResponse = mockTesRequestResponse(newTestResquest);

        Mockito.when(userLoggedInService.getLoggedInUser()).thenReturn(mockDoctor);
        Mockito.when(testRequestUpdateService.assignForLabTest(-123L,mockDoctor)).thenThrow(new AppException("Invalid Data"));

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, ()-> {
            labRequestController.assignForLabTest(-123l);
        });

        //Assert
        assertNotNull(exception);
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        assertEquals("Invalid Data",exception.getReason());
    }


    public User createMockTester(){
        User mockTester = new User();
        mockTester.setId(102L);
        mockTester.setLastName("Doctor");
        mockTester.setLastName("Name");
        mockTester.setUserName("userName");
        return mockTester;
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
    @Autowired
    LabRequestController labRequestController;

    @Autowired
    TestRequestQueryService testRequestQueryService;

    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_update_the_request_status() {
        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.INITIATED);

        //Implement this method

        //Create another object of the TestRequest method and explicitly assign this object for Lab Test using assignForLabTest() method
        // from labRequestController class. Pass the request id of testRequest object.

        TestRequest newTestRequest = new TestRequest();
        //Act
        newTestRequest = labRequestController.assignForLabTest(testRequest.getRequestId());

        //Use assertThat() methods to perform the following two comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'INITIATED'
        // make use of assertNotNull() method to make sure that the lab result of second object is not null
        // use getLabResult() method to get the lab result

        //Assert
        assertThat("Request IDs on the 2 objects are not same", testRequest.getRequestId().equals(newTestRequest.getRequestId()));
        assertThat("2nd test request status is not Initiated", newTestRequest.getStatus().toString().equals(RequestStatus.INITIATED.toString()));
        //System.out.println("Status of second request is: " + newTestRequest.getStatus().toString());
        assertNotNull(newTestRequest.getLabResult());
    }

    public TestRequest getTestRequestByStatus(RequestStatus status) {
        return testRequestQueryService.findBy(status).stream().findFirst().get();
    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_assignForLabTest_with_valid_test_request_id_should_throw_exception() {
        //Arrange
        Long InvalidRequestId = -34L;

        //Implement this method


        // Create an object of ResponseStatusException . Use assertThrows() method and pass assignForLabTest() method
        // of labRequestController with InvalidRequestId as Id

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            TestRequest testRequest = labRequestController.assignForLabTest(InvalidRequestId);
        });

        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

        //Assert
        assertThat(exception.getMessage(), containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_valid_test_request_id_should_update_the_request_status_and_update_test_request_details() {

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

        //Implement this method
        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter

        CreateLabResult createLabResult = getCreateLabResult(testRequest);
        //Create another object of the TestRequest method and explicitly update the status of this object
        // to be 'LAB_TEST_IN_PROGRESS'. Make use of updateLabTest() method from labRequestController class (Pass the previously created two objects as parameters)
        TestRequest newTestRequest = new TestRequest();

        //Act
        newTestRequest.setStatus(RequestStatus.LAB_TEST_IN_PROGRESS);
        newTestRequest.setStatus(labRequestController.updateLabTest(testRequest.getRequestId(), createLabResult).getStatus());

        //Use assertThat() methods to perform the following three comparisons
        //  1. the request ids of both the objects created should be same
        //  2. the status of the second object should be equal to 'LAB_TEST_COMPLETED'
        // 3. the results of both the objects created should be same. Make use of getLabResult() method to get the results.

        //Assert
        assertThat("Request IDs on the 2 test request objects are not same: " + testRequest.getRequestId()  + " " + newTestRequest.getRequestId(), testRequest.getRequestId().equals(newTestRequest.getRequestId()));
        assertThat("Status of the second object is not LAB_TEST_COMPLETED: "+ newTestRequest.getStatus(), RequestStatus.LAB_TEST_COMPLETED.toString().equals(newTestRequest.getStatus().toString()));
        assertThat("Lab results on both the test request objects are not same: " + testRequest.getLabResult().getResult() + " " + newTestRequest.getLabResult().getResult(), testRequest.getLabResult().getResult().equals(newTestRequest.getLabResult().getResult()));
    }


    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_test_request_id_should_throw_exception() {

        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);


        //Implement this method

        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        CreateLabResult createLabResult = getCreateLabResult(testRequest);

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with a negative long value as Id and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            TestRequest newTestRequest = labRequestController.updateLabTest(-123L,createLabResult);
        });


        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "Invalid ID"

        //Assert
        assertThat(exception.getMessage(), containsString("Invalid ID"));

    }

    @Test
    @WithUserDetails(value = "tester")
    public void calling_updateLabTest_with_invalid_empty_status_should_throw_exception() {
        //Arrange
        TestRequest testRequest = getTestRequestByStatus(RequestStatus.LAB_TEST_IN_PROGRESS);

        //Implement this method

        //Create an object of CreateLabResult and call getCreateLabResult() to create the object. Pass the above created object as the parameter
        // Set the result of the above created object to null.
        CreateLabResult createLabResult = getCreateLabResult(testRequest);
        createLabResult.setResult(null);

        // Create an object of ResponseStatusException . Use assertThrows() method and pass updateLabTest() method
        // of labRequestController with request Id of the testRequest object and the above created object as second parameter
        //Refer to the TestRequestControllerTest to check how to use assertThrows() method

        //Act
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            TestRequest newTestRequest = labRequestController.updateLabTest(testRequest.getRequestId(),createLabResult);
        });

        //Use assertThat() method to perform the following comparison
        //  the exception message should be contain the string "ConstraintViolationException"

        //Assert
        assertThat(exception.getMessage(), containsString("ConstraintViolationException"));

    }

    public CreateLabResult getCreateLabResult(TestRequest testRequest) {

        //Create an object of CreateLabResult and set all the values
        // Return the object
        CreateLabResult newLabResult = new CreateLabResult();
        newLabResult.setBloodPressure("120");
        newLabResult.setComments("Results Updated");
        newLabResult.setHeartBeat("72");
        newLabResult.setOxygenLevel("95");
        newLabResult.setTemperature("98");
        newLabResult.setResult(TestStatus.NEGATIVE);
        return newLabResult; // Replace this line with your code
    }*/

}