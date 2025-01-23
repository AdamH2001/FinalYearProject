# Create schemas
USE after_school_club;
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE `after_school_club`.`attendee`, `after_school_club`.`Attendee_Menu_Choice`, `after_school_club`.`class`, `after_school_club`.`event`, `after_school_club`.`Event_Menu`, `after_school_club`.`Event_Resource`, `after_school_club`.`incident`, `after_school_club`.`Medical_Note`, `after_school_club`.`Menu_Group`, `after_school_club`.`Menu_Group_Option`, `after_school_club`.`Menu_Option`, `after_school_club`.`parent`, `after_school_club`.`resource`, `after_school_club`.`student`, `after_school_club`.`Parental_Transaction`, `after_school_club`.`user`;
SET FOREIGN_KEY_CHECKS=1;


# Create tables
CREATE TABLE IF NOT EXISTS User
(
    user_id INT AUTO_INCREMENT  NOT NULL,
    email VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    surname VARCHAR(64) NOT NULL,
    validation_key INT,
    date_requested DATETIME NOT NULL,
    email_verified BOOLEAN NOT NULL,
    PRIMARY KEY(user_id)
);

CREATE TABLE IF NOT EXISTS Event
(
    event_id INT AUTO_INCREMENT  NOT NULL,
    title VARCHAR(64) NOT NULL,
    description VARCHAR(256),
    location VARCHAR(256) NOT NULL,
    start_date_time DATETIME NOT NULL,
    end_date_time DATETIME NOT NULL,
    base_price INT NOT NULL,
    max_attendees INT,
    year_r_can_attend BOOLEAN NOT NULL,
    year_one_can_attend BOOLEAN NOT NULL,
    year_two_can_attend BOOLEAN NOT NULL,
    year_three_can_attend BOOLEAN NOT NULL,
    year_four_can_attend BOOLEAN NOT NULL,
    year_five_can_attend BOOLEAN NOT NULL,
    year_six_can_attend BOOLEAN NOT NULL,
    PRIMARY KEY(event_id)
);

CREATE TABLE IF NOT EXISTS Parental_Transaction
(
    transaction_id INT AUTO_INCREMENT  NOT NULL,
    amount INT NOT NULL,
    date_time DATETIME NOT NULL,
    transaction_type VARCHAR(1) NOT NULL,
    description VARCHAR(256) NOT NULL,
    parent_id INT NOT NULL,
    PRIMARY KEY(transaction_id)
);

CREATE TABLE IF NOT EXISTS Parent
(
    parent_id INT AUTO_INCREMENT  NOT NULL,
    user_id INT NOT NULL,
    balance INT NOT NULL,
    telephone_num VARCHAR(20) NOT NULL,
    alt_contact_name VARCHAR(128) NOT NULL,
    alt_telephone_num VARCHAR(20) NOT NULL,
    PRIMARY KEY(parent_id)
);

CREATE TABLE IF NOT EXISTS Student
(
    student_id INT AUTO_INCREMENT  NOT NULL,
    parent_id INT NOT NULL,
    class_id INT NOT NULL,
    first_name VARCHAR(64) NOT NULL,
    surname VARCHAR(64) NOT NULL,
    date_of_birth DATE NOT NULL,
    health_questionnaire_completed DATETIME NOT NULL,
    consent_to_share BOOLEAN NOT NULL,
    PRIMARY KEY(student_id)
);

CREATE TABLE IF NOT EXISTS Attendee
(
    attendee_id INT AUTO_INCREMENT  NOT NULL,
    event_id INT NOT NULL,
    student_id INT NOT NULL,
    attended BOOLEAN,
    PRIMARY KEY(attendee_id)
);

CREATE TABLE IF NOT EXISTS Incident
(
    incident_id INT AUTO_INCREMENT  NOT NULL,
    event_id INT NOT NULL,
    attendee_id INT NOT NULL,
    summary VARCHAR(2000) NOT NULL,
    PRIMARY KEY(incident_id)
);

CREATE TABLE IF NOT EXISTS Resource
(
    resource_id INT AUTO_INCREMENT  NOT NULL,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(256) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY(resource_id)
);

CREATE TABLE IF NOT EXISTS Event_Resource
(
    event_resource_id INT AUTO_INCREMENT  NOT NULL,
    event_id INT NOT NULL,
    resource_id INT NOT NULL,
    quantity INT NOT NULL,
    type VARCHAR(1) NOT NULL,
    PRIMARY KEY(event_resource_id)
);

CREATE TABLE IF NOT EXISTS Menu_Option
(
    menu_option_id INT AUTO_INCREMENT  NOT NULL,
    name VARCHAR(64) NOT NULL,
    description VARCHAR(256) NOT NULL,
    additional_cost INT NOT NULL,
    allergy_information VARCHAR(1024),
    PRIMARY KEY(menu_option_id)
);

CREATE TABLE IF NOT EXISTS Menu_Group
(
    menu_group_id INT AUTO_INCREMENT  NOT NULL,
    name VARCHAR(64) NOT NULL,
    PRIMARY KEY(menu_group_id)
);

CREATE TABLE IF NOT EXISTS Class
(
    class_id INT AUTO_INCREMENT  NOT NULL,
    name VARCHAR(32) NOT NULL,
    year_group INT NOT NULL,
    PRIMARY KEY(class_id)
);

CREATE TABLE IF NOT EXISTS Medical_Note
(
    medical_note_id INT AUTO_INCREMENT  NOT NULL,
    student_id INT NOT NULL,
    note_type VARCHAR(1) NOT NULL,
    note VARCHAR(1024) NOT NULL,
    PRIMARY KEY(medical_note_id)
);

CREATE TABLE IF NOT EXISTS Menu_Group_Option
(
    menu_group_option_id INT AUTO_INCREMENT  NOT NULL,
    menu_option_id INT NOT NULL,
    menu_group_id INT NOT NULL,
    PRIMARY KEY(menu_group_option_id)
);

CREATE TABLE IF NOT EXISTS Event_Menu
(
    event_menu_id INT AUTO_INCREMENT  NOT NULL,
    event_id INT NOT NULL,
    menu_group_id INT NOT NULL,
    PRIMARY KEY(event_menu_id)
);

CREATE TABLE IF NOT EXISTS Attendee_Menu_Choice
(
    attendee_menu_choice_id INT AUTO_INCREMENT  NOT NULL,
    attendee_id INT NOT NULL,
    menu_option_id INT NOT NULL,
    PRIMARY KEY(attendee_menu_choice_id)
);


# Create FKs
ALTER TABLE Student
    ADD    FOREIGN KEY (parent_id)
    REFERENCES Parent(parent_id)
;
    
ALTER TABLE Parent
    ADD    FOREIGN KEY (user_id)
    REFERENCES User(user_id)
;
    
ALTER TABLE Attendee
    ADD    FOREIGN KEY (student_id)
    REFERENCES Student(student_id)
;
    
ALTER TABLE Attendee
    ADD    FOREIGN KEY (event_id)
    REFERENCES Event(event_id)
;
    
ALTER TABLE Parental_Transaction
    ADD    FOREIGN KEY (parent_id)
    REFERENCES Parent(parent_id)
;
    
ALTER TABLE Incident
    ADD    FOREIGN KEY (event_id)
    REFERENCES Event(event_id)
;
    
ALTER TABLE Incident
    ADD    FOREIGN KEY (attendee_id)
    REFERENCES Attendee(attendee_id)
;
    
ALTER TABLE Event_Resource
    ADD    FOREIGN KEY (event_id)
    REFERENCES Event(event_id)
;
    
ALTER TABLE Event_Resource
    ADD    FOREIGN KEY (resource_id)
    REFERENCES Resource(resource_id)
;
    
ALTER TABLE Student
    ADD    FOREIGN KEY (class_id)
    REFERENCES Class(class_id)
;
    
ALTER TABLE Medical_Note
    ADD    FOREIGN KEY (student_id)
    REFERENCES Student(student_id)
;
    
ALTER TABLE Menu_Group_Option
    ADD    FOREIGN KEY (menu_group_id)
    REFERENCES Menu_Group(menu_group_id)
;
    
ALTER TABLE Menu_Group_Option
    ADD    FOREIGN KEY (menu_option_id)
    REFERENCES Menu_Option(menu_option_id)
;
    
ALTER TABLE Event_Menu
    ADD    FOREIGN KEY (event_id)
    REFERENCES Event(event_id)
;
    
ALTER TABLE Event_Menu
    ADD    FOREIGN KEY (menu_group_id)
    REFERENCES Menu_Group(menu_group_id)
;
    
ALTER TABLE Attendee_Menu_Choice
    ADD    FOREIGN KEY (attendee_id)
    REFERENCES Attendee(attendee_id)
;
    
ALTER TABLE Attendee_Menu_Choice
    ADD    FOREIGN KEY (menu_option_id)
    REFERENCES Menu_Option(menu_option_id)
;

# Inserts
INSERT into after_school_club.class (name,year_group)
VALUES ("Year R", 0)
,("Year 1", 1)
,("Year 2", 2)
,("Year 3", 3)
,("Year 4", 4)
,("Year 5", 5)
,("Year 6", 6);

INSERT into after_school_club.menu_group (name)
VALUES ("Drinks")
,("Food")
,("Fruit");

INSERT into after_school_club.menu_option (name,description,additional_cost,allergy_information)
VALUES ("Milk","A glass of milk",50,"Contains dairy")
,("Water","A glass of water",0,"N/A")
,("Orange Juice","A glass of orange juice",100,"Contains real fruit")
,("Ham Sandwich","Buttered ham Sandwich",100,"Contains dairy")
,("Cheese Sandwich","Buttered cheese Sandwich",100,"Contains dairy")
,("Bacon Sandwich","Roll with Bacon",200,"Contains pork meat")
,("Apple","A pink lady apple",50,"Contains Mal d 1")
,("Banana","A fairtrade banana",40,"Contains proteins found in latex")
,("Pear","A fairtrade pear",30,"N/A");

INSERT into after_school_club.menu_group_option(menu_option_id, menu_group_id)
VALUES ((SELECT menu_option_id from after_school_club.menu_option WHERE name="Milk"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Drinks"))
,((SELECT menu_option_id from after_school_club.menu_option WHERE name="Water"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Drinks"))
,((SELECT menu_option_id from after_school_club.menu_option WHERE name="Orange Juice"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Drinks"))
,((SELECT menu_option_id from after_school_club.menu_option WHERE name="Ham Sandwich"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Food"))
,((SELECT menu_option_id from after_school_club.menu_option WHERE name="Cheese Sandwich"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Food"))
,((SELECT menu_option_id from after_school_club.menu_option WHERE name="Bacon Sandwich"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Food"))
,((SELECT menu_option_id from after_school_club.menu_option WHERE name="Apple"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Fruit"))
,((SELECT menu_option_id from after_school_club.menu_option WHERE name="Banana"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Fruit"))
,((SELECT menu_option_id from after_school_club.menu_option WHERE name="Pear"),(SELECT menu_group_id from after_school_club.menu_group WHERE name="Fruit"));

INSERT into after_school_club.user (email,password,first_name,surname,validation_key,date_requested,email_verified)
VALUES ("adam@hattonsplace.co.uk","QmFzaW5nc3Rva2UxMg==","Adam","Hatton","6000000",'2022-12-27',True)
,("peterjones@hattonsplace.co.uk","VGVzdA==","Peter","Jones","6000000",'2022-12-27',True);

INSERT into after_school_club.parent (user_id,balance,telephone_num,alt_contact_name,alt_telephone_num)
VALUES ((SELECT user_id from after_school_club.user WHERE first_name="Peter"),0,"012345","Smithy","1234");

# Create Indexes
