
-- Script to create initial database tables and setup initial
-- administrator user and holiday dates

USE after_school_club2;
SET FOREIGN_KEY_CHECKS=0;


-- Drop all the tables if they already exist

DROP TABLE IF EXISTS `attendee`, `Attendee_Menu_Choice`, `Holiday`, 
	`Recurrence_Specification`, `class`,  `club`, `Session`, 
	`Session_Menu`, `Session_Resource`, `incident`, `Attendee_Incident`,  
	`Medical_Note`, `Menu_Group`, `Menu_Group_Option`, `Menu_Option`,
	`parent`, `resource`, `student`, 
	`Parental_Transaction`, `user`;
	
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
  `email_verified` BOOLEAN NOT NULL,
  `admin_verified` BOOLEAN NOT NULL,
  `state` ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE' 

);

CREATE TABLE `Club` (
  `club_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `title` VARCHAR(64) NOT NULL,
  `description` VARCHAR(1024) NOT NULL,
  `keywords` VARCHAR(256) NOT NULL,
  `base_price` INT NOT NULL,
  `accepts_vouchers` BOOLEAN NOT NULL,  
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
  `recurrence_specification_id` INT PRIMARY KEY 
  					NOT NULL AUTO_INCREMENT,
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

CREATE TABLE `Session` (
  `session_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
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
  `club_id` INT,
  `payment_reference` VARCHAR(256),  
  `balance_type` ENUM('VOUCHER','CASH') NOT NULL,
  `amount` INT NOT NULL,
  `date_time` DATETIME NOT NULL,
  `transaction_type` 
  		ENUM('DEPOSIT','WITHDRAWAL','REFUND','PAYMENT') NOT NULL,
  `description` VARCHAR(1024) NOT NULL,
  `parent_id` INT NOT NULL
);

CREATE TABLE `Parent` (
  `parent_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,  
  `alt_contact_name` VARCHAR(128) NOT NULL,
  `alt_telephone_num` VARCHAR(20) NOT NULL,
  `overdraft_limit` INT NOT NULL default 0
);

CREATE TABLE `Student` (
  `student_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `parent_id` INT NOT NULL,
  `class_id` INT NOT NULL,
  `first_name` VARCHAR(64) NOT NULL,
  `surname` VARCHAR(64) NOT NULL,
  `date_of_birth` DATE NOT NULL,
  `health_questionnaire_completed` DATETIME NOT NULL,
  `consent_to_share` BOOLEAN NOT NULL,
  `date_requested` DATETIME NOT NULL,
  `admin_verified` BOOLEAN NOT NULL,
  `state` ENUM('ACTIVE','INACTIVE') NOT NULL DEFAULT 'ACTIVE'   
);

CREATE TABLE `Attendee` (
  `attendee_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `session_id` INT NOT NULL,
  `student_id` INT NOT NULL,
  `attended` ENUM('ABSENT','PRESENT','NOTRECORDED') NOT NULL
);


CREATE TABLE `Incident` (
  `incident_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `session_id` INT NOT NULL,
  `summary` VARCHAR(2000) NOT NULL
);

CREATE TABLE `Attendee_Incident` (
  `attendee_incident_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `incident_id` INT NOT NULL,
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

CREATE TABLE `Session_Resource` (
  `session_resource_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `session_id` INT NOT NULL,
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
  `note_type` ENUM('ALLERGY','HEALTH','DIET','CAREPLAN',
  		'MEDICATION','OTHER') NOT NULL,
  `note` VARCHAR(1024) NOT NULL
);

CREATE TABLE `Menu_Group_Option` (
  `menu_group_option_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `menu_option_id` INT NOT NULL,
  `menu_group_id` INT NOT NULL,
  `state` ENUM('ACTIVE','INACTIVE') NOT NULL  

);

CREATE TABLE `Session_Menu` (
  `session_menu_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `session_id` INT NOT NULL,
  `menu_group_id` INT NOT NULL
);

CREATE TABLE `Attendee_Menu_Choice` (
  `attendee_menu_choice_id` INT PRIMARY KEY NOT NULL AUTO_INCREMENT,
  `attendee_id` INT NOT NULL,
  `menu_group_option_id` INT NOT NULL
);

-- Add all the foreign key relations

ALTER TABLE `Student` ADD FOREIGN KEY (`parent_id`) 
	REFERENCES `Parent` (`parent_id`);

ALTER TABLE `Parent` ADD FOREIGN KEY (`user_id`) 
	REFERENCES `User` (`user_id`);

ALTER TABLE `Attendee` ADD FOREIGN KEY (`student_id`) 
	REFERENCES `Student` (`student_id`);

ALTER TABLE `Attendee` ADD FOREIGN KEY (`session_id`) 
	REFERENCES `Session` (`session_id`);

ALTER TABLE `Parental_Transaction` ADD FOREIGN KEY (`parent_id`) 
	REFERENCES `Parent` (`parent_id`);

ALTER TABLE `Incident` ADD FOREIGN KEY (`session_id`) 
	REFERENCES `Session` (`session_id`);

ALTER TABLE `Attendee_Incident` ADD FOREIGN KEY (`incident_id`) 
	REFERENCES `Incident` (`incident_id`);

ALTER TABLE `Attendee_Incident` ADD FOREIGN KEY (`attendee_id`) 
	REFERENCES `Attendee` (`attendee_id`);

ALTER TABLE `Session_Resource` ADD FOREIGN KEY (`session_id`) 
	REFERENCES `Session` (`session_id`);

ALTER TABLE `Session_Resource` ADD FOREIGN KEY (`resource_id`) 
	REFERENCES `Resource` (`resource_id`);

ALTER TABLE `Student` ADD FOREIGN KEY (`class_id`) 
	REFERENCES `Class` (`class_id`);

ALTER TABLE `Medical_Note` ADD FOREIGN KEY (`student_id`) 
	REFERENCES `Student` (`student_id`);

ALTER TABLE `Menu_Group_Option` ADD FOREIGN KEY (`menu_group_id`) 
	REFERENCES `Menu_Group` (`menu_group_id`);

ALTER TABLE `Menu_Group_Option` ADD FOREIGN KEY (`menu_option_id`) 
	REFERENCES `Menu_Option` (`menu_option_id`);

ALTER TABLE `Session_Menu` ADD FOREIGN KEY (`session_id`) 
	REFERENCES `Session` (`session_id`);

ALTER TABLE `Session_Menu` ADD FOREIGN KEY (`menu_group_id`) 
	REFERENCES `Menu_Group` (`menu_group_id`);

ALTER TABLE `Attendee_Menu_Choice` ADD FOREIGN KEY (`attendee_id`) 
	REFERENCES `Attendee` (`attendee_id`);

ALTER TABLE `Attendee_Menu_Choice` ADD FOREIGN KEY (`menu_group_option_id`) 
	REFERENCES `Menu_Group_Option` (`menu_group_option_id`);

ALTER TABLE `Session` ADD FOREIGN KEY (`club_id`) 
	REFERENCES `Club` (`club_id`);

ALTER TABLE `Resource` ADD FOREIGN KEY (`user_id`) 
	REFERENCES `User` (`user_id`);

ALTER TABLE `Session` ADD FOREIGN KEY (`recurrence_specification_id`) 
	REFERENCES `Recurrence_Specification` (`recurrence_specification_id`);

ALTER TABLE `Parental_Transaction` ADD FOREIGN KEY (`club_id`) 
	REFERENCES `Club` (`club_id`);

-- Insert the default class structures

INSERT into after_school_club2.class (name,year_group)
VALUES ("Year R", 0)
,("Year 1", 1)
,("Year 2", 2)
,("Year 3", 3)
,("Year 4", 4)
,("Year 5", 5)
,("Year 6", 6);

-- Insert the default holidays for academic year 25/26

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

-- Insert the a default adminsitrator account and set up as a resource             
            
INSERT into after_school_club2.user alter(email,password,first_name,surname,
	validation_key,date_requested,email_verified, admin_verified, 
	telephone_num, title)
VALUES ("admin@afterschool-club.com",
	"$2a$12$BxEkNwY9N4klFidhNOC9ieLljbn3IEaDFiGHROEjBU4.m3vWv4sS6",
	"Adam","Hatton","6000000",'2025-03-31',True, True,
	"01256812734", "Mr");

INSERT into after_school_club2.resource(name, description, 
	quantity, type, keywords, state, capacity, user_id)
VALUES ("Mr A Hatton", "Club Administrator", 1, 
	"STAFF", "Finance Club Administration",  'ACTIVE', 30, 
	(SELECT user_id from user  WHERE first_name="Adam"));
       
