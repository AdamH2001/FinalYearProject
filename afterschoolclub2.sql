USE after_school_club2;
SET FOREIGN_KEY_CHECKS=0;

DROP TABLE `attendee`, `Attendee_Menu_Choice`, `Holiday`, `Recurrence_Specification`, `class`,  `club`, `event`, `Event_Menu`, `Event_Resource`, `incident`, `Medical_Note`, `Menu_Group`, `Menu_Group_Option`, `Menu_Option`, `parent`, `resource`, `student`, `Parental_Transaction`, `user`;
SET FOREIGN_KEY_CHECKS=1;


CREATE TABLE `User` (
  `user_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `email` VARCHAR(64) UNIQUE NOT NULL,
  `password` VARCHAR(256) NOT NULL,
  `first_name` VARCHAR(64) NOT NULL,
  `surname` VARCHAR(64) NOT NULL,
  `telephone_num` VARCHAR(20) NOT NULL,
  `title` VARCHAR(20) NOT NULL,
  `approval_key` INT,
  `validation_key` INT,
  `date_requested` DATETIME NOT NULL,
  `email_verified` BOOLEAN NOT NULL
);

CREATE TABLE `Club` (
  `club_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(64) NOT NULL,
  `description` VARCHAR(1024),
  `base_price` INT NOT NULL,
  `year_r_can_attend` BOOLEAN NOT NULL,
  `year_1_can_attend` BOOLEAN NOT NULL,
  `year_2_can_attend` BOOLEAN NOT NULL,
  `year_3_can_attend` BOOLEAN NOT NULL,
  `year_4_can_attend` BOOLEAN NOT NULL,
  `year_5_can_attend` BOOLEAN NOT NULL,
  `year_6_can_attend` BOOLEAN NOT NULL,
  `state` ENUM('ACTIVE','INACTIVE') NOT NULL  
);

CREATE TABLE `Recurrence_Specification` (
  `recurrence_specification_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `start_date` DATE NOT NULL,
  `end_date` DATE NOT NULL,
  `occurs_monday` BOOLEAN NOT NULL,
  `occurs_tuesday` BOOLEAN NOT NULL,
  `occurs_wednesday` BOOLEAN NOT NULL,
  `occurs_thursday` BOOLEAN NOT NULL,
  `occurs_friday` BOOLEAN NOT NULL,
  `occurs_saturday` BOOLEAN NOT NULL,
  `occurs_sunday` BOOLEAN NOT NULL,
  `term_time_only` BOOLEAN NOT NULL
);

CREATE TABLE `Event` (
  `event_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `recurrence_specification_id` INT,
  `club_id` INT NOT NULL,
  `start_date_time` DATETIME NOT NULL,
  `end_date_time` DATETIME NOT NULL,
  `administrator_notes` VARCHAR(1024),
  `parent_notes` VARCHAR(1024),
  `max_attendees` INT
);

CREATE TABLE `Holiday` (
  `holiday_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `start_date` DATE,
  `end_date` DATE
);

CREATE TABLE `Parental_Transaction` (
  `transaction_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `balance_type` ENUM('VOUCHER','CASH') NOT NULL,
  `amount` INT NOT NULL,
  `date_time` DATETIME NOT NULL,
  `transaction_type` ENUM('DEPOSIT','WITHDRAWAL','REFUND','PAYMENT') NOT NULL,
  `description` VARCHAR(1024) NOT NULL,
  `parent_id` INT NOT NULL
);

CREATE TABLE `Parent` (
  `parent_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,  
  `alt_contact_name` VARCHAR(128) NOT NULL,
  `alt_telephone_num` VARCHAR(20) NOT NULL
);

CREATE TABLE `Student` (
  `student_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `parent_id` INT NOT NULL,
  `class_id` INT NOT NULL,
  `first_name` VARCHAR(64) NOT NULL,
  `surname` VARCHAR(64) NOT NULL,
  `date_of_birth` DATE NOT NULL,
  `health_questionnaire_completed` DATETIME NOT NULL,
  `consent_to_share` BOOLEAN NOT NULL
);

CREATE TABLE `Attendee` (
  `attendee_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `student_id` INT NOT NULL,
  `attended` ENUM('ABSENT','PRESENT','NOTRECORDED') NOT NULL
);

CREATE TABLE `Incident` (
  `incident_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `attendee_id` INT NOT NULL,
  `summary` VARCHAR(2000) NOT NULL
);

CREATE TABLE `Resource` (
  `resource_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(64) NOT NULL,
  `description` VARCHAR(1024) NOT NULL,
  `quantity` INT NOT NULL,
  `type` ENUM('LOCATION','STAFF','EQUIPMENT') NOT NULL,
  `state` ENUM('ACTIVE','INACTIVE') NOT NULL,
  `capacity` INT NOT NULL,
  `keywords` VARCHAR(256) NOT NULL,
  `user_id` INT
);

CREATE TABLE `Event_Resource` (
  `event_resource_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `resource_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `per_attendee` BOOLEAN NOT NULL
);

CREATE TABLE `Menu_Option` (
  `menu_option_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(64) NOT NULL,
  `description` VARCHAR(1024) NOT NULL,
  `additional_cost` INT NOT NULL,
  `allergy_information` VARCHAR(1024),
  `state` ENUM('ACTIVE','INACTIVE') NOT NULL  
);

CREATE TABLE `Menu_Group` (
  `menu_group_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(64) NOT NULL,
  `state` ENUM('ACTIVE','INACTIVE') NOT NULL  
);

CREATE TABLE `Class` (
  `class_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL,
  `year_group` INT NOT NULL
);

CREATE TABLE `Medical_Note` (
  `medical_note_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `student_id` INT NOT NULL,
  `note_type` ENUM('ALLERGY','HEALTH','DIET','CAREPLAN','MEDICATION','OTHER') NOT NULL,
  `note` VARCHAR(1024) NOT NULL
);

CREATE TABLE `Menu_Group_Option` (
  `menu_group_option_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `menu_option_id` INT NOT NULL,
  `menu_group_id` INT NOT NULL,
  `state` ENUM('ACTIVE','INACTIVE') NOT NULL  

);

CREATE TABLE `Event_Menu` (
  `event_menu_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `event_id` INT NOT NULL,
  `menu_group_id` INT NOT NULL
);

CREATE TABLE `Attendee_Menu_Choice` (
  `attendee_menu_choice_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `attendee_id` INT NOT NULL,
  `menu_group_option_id` INT NOT NULL
);


ALTER TABLE `Student` ADD FOREIGN KEY (`parent_id`) REFERENCES `Parent` (`parent_id`);

ALTER TABLE `Parent` ADD FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`);

ALTER TABLE `Attendee` ADD FOREIGN KEY (`student_id`) REFERENCES `Student` (`student_id`);

ALTER TABLE `Attendee` ADD FOREIGN KEY (`event_id`) REFERENCES `Event` (`event_id`);

ALTER TABLE `Parental_Transaction` ADD FOREIGN KEY (`parent_id`) REFERENCES `Parent` (`parent_id`);

ALTER TABLE `Incident` ADD FOREIGN KEY (`event_id`) REFERENCES `Event` (`event_id`);

ALTER TABLE `Incident` ADD FOREIGN KEY (`attendee_id`) REFERENCES `Attendee` (`attendee_id`);

ALTER TABLE `Event_Resource` ADD FOREIGN KEY (`event_id`) REFERENCES `Event` (`event_id`);

ALTER TABLE `Event_Resource` ADD FOREIGN KEY (`resource_id`) REFERENCES `Resource` (`resource_id`);

ALTER TABLE `Student` ADD FOREIGN KEY (`class_id`) REFERENCES `Class` (`class_id`);

ALTER TABLE `Medical_Note` ADD FOREIGN KEY (`student_id`) REFERENCES `Student` (`student_id`);

ALTER TABLE `Menu_Group_Option` ADD FOREIGN KEY (`menu_group_id`) REFERENCES `Menu_Group` (`menu_group_id`);

ALTER TABLE `Menu_Group_Option` ADD FOREIGN KEY (`menu_option_id`) REFERENCES `Menu_Option` (`menu_option_id`);

ALTER TABLE `Event_Menu` ADD FOREIGN KEY (`event_id`) REFERENCES `Event` (`event_id`);

ALTER TABLE `Event_Menu` ADD FOREIGN KEY (`menu_group_id`) REFERENCES `Menu_Group` (`menu_group_id`);

ALTER TABLE `Attendee_Menu_Choice` ADD FOREIGN KEY (`attendee_id`) REFERENCES `Attendee` (`attendee_id`);

ALTER TABLE `Attendee_Menu_Choice` ADD FOREIGN KEY (`menu_group_option_id`) REFERENCES `Menu_Group_Option` (`menu_group_option_id`);

ALTER TABLE `Event` ADD FOREIGN KEY (`club_id`) REFERENCES `Club` (`club_id`);

ALTER TABLE `Resource` ADD FOREIGN KEY (`user_id`) REFERENCES `User` (`user_id`);

ALTER TABLE `Event` ADD FOREIGN KEY (`recurrence_specification_id`) REFERENCES `Recurrence_Specification` (`recurrence_specification_id`);

# Inserts
INSERT into after_school_club2.class (name,year_group)
VALUES ("Year R", 0)
,("Year 1", 1)
,("Year 2", 2)
,("Year 3", 3)
,("Year 4", 4)
,("Year 5", 5)
,("Year 6", 6);

INSERT into after_school_club2.menu_group (name)
VALUES ("Drinks Selection")
,("Breakfast Menu")
,("Fruit Selection");

INSERT into after_school_club2.menu_option (name,description,additional_cost,allergy_information)
VALUES ("Milk","A glass of milk",50,"Contains dairy")
,("Water","A glass of water",0,"N/A")
,("Orange Juice","A glass of orange juice",100,"Contains real fruit")
,("Ham Sandwich","Buttered ham Sandwich",100,"Contains dairresourcey")
,("Cheese Sandwich","Buttered cheese Sandwich",100,"Contains dairy")
,("Bacon Sandwich","Roll with Bacon",200,"Contains pork meat")
,("Apple","A pink lady apple",50,"Contains Mal d 1")
,("Banana","A fairtrade banana",40,"Contains proteins found in latex")
,("Pear","A fairtrade pear",30,"N/A");

INSERT into after_school_club2.menu_group_option(menu_option_id, menu_group_id)
VALUES ((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Milk"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Drinks Selection"))
,((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Water"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Drinks Selection"))
,((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Orange Juice"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Drinks Selection"))
,((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Ham Sandwich"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Breakfast Menu"))
,((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Cheese Sandwich"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Breakfast Menu"))
,((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Bacon Sandwich"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Breakfast Menu"))
,((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Apple"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Fruit Selection"))
,((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Banana"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Fruit Selection"))
,((SELECT menu_option_id from after_school_club2.menu_option WHERE name="Pear"),(SELECT menu_group_id from after_school_club2.menu_group WHERE name="Fruit Selection"));

INSERT into after_school_club2.user (email,password,first_name,surname,validation_key,date_requested,email_verified, telephone_num, title)
VALUES ("adam@hattonsplace.co.uk","TWFuVXRkMDE=","Adam","Hatton","6000000",'2022-12-27',True, "01256812734", "Mr"),
("chris@hattonsplace.co.uk","TWFuVXRkMDE=","Christine","Smith","6000000",'2022-12-27',True,"01256812734", "Mrs"),
("peterjones@hattonsplace.co.uk","TWFuVXRkMDE=","Peter","Jones","6000000",'2022-12-27',True, "01256812734", "Mr");

 
INSERT into after_school_club2.resource(name, description, quantity, type, keywords, state, capacity, user_id)
VALUES ("Mr A Hatton", "Sports Teacher", 1, "STAFF", "sport",  'ACTIVE', 30, (SELECT user_id from user  WHERE first_name="Adam")),
 ("Mrs C Smith", "Computer Teacher", 1, "STAFF", "technology", 'ACTIVE', 30, (SELECT user_id from user  WHERE first_name="Christine"));
        
        


INSERT into after_school_club2.parent (user_id, alt_contact_name,alt_telephone_num)
VALUES ((SELECT user_id from after_school_club2.user WHERE first_name="Peter"),"Smithy","1234");

INSERT into after_school_club2.student (parent_id, class_id, first_name, surname, date_of_birth, health_questionnaire_completed, consent_to_share)
VALUES ((SELECT parent_id from after_school_club2.parent WHERE alt_telephone_num="1234"), (SELECT class_id from after_school_club2.class WHERE year_group=6), "Ruth", "Jones", "2014-01-01", "2025-01-01", true), 
		((SELECT parent_id from after_school_club2.parent WHERE alt_telephone_num="1234"), (SELECT class_id from after_school_club2.class WHERE year_group=0), "Jonny", "Jones", "2019-02-01", "2025-01-01", true), 
		((SELECT parent_id from after_school_club2.parent WHERE alt_telephone_num="1234"), (SELECT class_id from after_school_club2.class WHERE year_group=6), "Tom", "Jones", "2014-01-01", "2025-01-01", true); 
        


INSERT into after_school_club2.club(title, description, base_price, year_r_can_attend, year_1_can_attend, year_2_can_attend, year_3_can_attend, year_4_can_attend, year_5_can_attend, year_6_can_attend)
VALUES ("Football Club", "Football Club for Year 6", 250, false, false, false, false, false, false, true );
	
INSERT into after_school_club2.resource(name, description, quantity, type, keywords, state, capacity)
VALUES ("Football Pitch 1", "Left hand football pitch at the back of the main building", 1, "LOCATION", "sport football", 'ACTIVE', 20),
	("Football Pitch 2", "Right hand football pitch at the back of the main building", 1, "LOCATION", "sport football", 'ACTIVE', 30),
	("Canteen", "Main canteen", 1, "LOCATION", "food",  'ACTIVE', 100),
    ("Music Room", "Room linked to assembly hall", 1, "LOCATION", "music classroom", 'ACTIVE', 15),
    ("Classroom 1", "First classroom off the main corridor", 1, "LOCATION", "classroom", 'ACTIVE', 30),
    ("Sports Hall", "The gym", 1, "LOCATION", "classroom",  'ACTIVE', 50),
    ("Classroom 2", "Second classroom off the main corridor", 1, "LOCATION", "classroom", 'ACTIVE', 30);
    

              
        
INSERT into after_school_club2.resource(name, description, quantity, type, keywords, state, capacity)
VALUES ("Football", "Regular sized football", 10, "Equipment", "sport football ball", 'ACTIVE', 0),
		("Red Bibs", "Red bibs", 30, "Equipment", "sport red bib", 'ACTIVE', 0),
        ("Blue Bibs", "Blue bibs", 30, "Equipment", "sport blue bib", 'ACTIVE', 0),
        ("Yellow Bibs", "Yellow bibs", 30, "Equipment", "sport yellow bib", 'ACTIVE', 0),
        ("Green Bibs", "Green bibs", 30, "Equipment", "sport green bib", 'ACTIVE', 0),
		("Football Goal", "Football Goal", 4, "Equipment", "football goal", 'ACTIVE', 0),
        ("Violin", "Violin", 5, "Equipment", "violin music", 'ACTIVE', 0),
        ("Guitar", "guitar", 3, "Equipment", "guitar music", 'ACTIVE', 0),
        ("Recorder", "recorder", 10, "Equipment", "record music", 'ACTIVE', 0);
        
INSERT into after_school_club2.holiday(start_date, end_date) 
	VALUES ("2025-4-7", "2025-4-21"), 
			("2025-5-26", "2025-5-30"), 
            ("2025-7-23", "2025-9-2"), 
            ("2025-10-27", "2025-10-31"), 
            ("2025-12-22", "2026-1-2"), 
            ("2026-2-16", "2026-2-20"), 
            ("2026-3-30", "2026-4-10"), 
            ("2026-5-25", "2026-5-29"), 
            ("2026-7-23", "2026-8-31");
            
            
            