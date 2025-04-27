package com.afterschoolclub;

import org.junit.jupiter.api.ClassOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestClassOrder;




@TestClassOrder(ClassOrderer.OrderAnnotation.class)

public class AfterSchoolClubTests {
    @Nested
    @Order(10)
    class TestStaffResourcesMenus extends AfterSchoolClubApplicationTests {}

    @Nested
    @Order(20)
    class TestSessions extends AfterSchoolClubApplicationTests2 {}

    @Nested
    @Order(30)
    class TestUserRegistration extends AfterSchoolClubApplicationTests3 {}

    @Nested
    @Order(40)
    class BookSessions extends AfterSchoolClubApplicationTests4 {}

    @Nested
    @Order(50)
    class TakeRegisterRecordIncidents extends AfterSchoolClubApplicationTests5 {}

    @Nested
    @Order(60)
    class UtiliseVoucherAndOverdraft extends AfterSchoolClubApplicationTests6 {}
    
    

}