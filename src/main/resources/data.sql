INSERT INTO HOUSEHOLD DEFAULT VALUES;

INSERT INTO RESIDENT (PERSON_ID, DATE_OF_BIRTH, FIRST_NAME, MOVE_ALLOWED, OFFICIAL_NAME, SEX, HOUSEHOLD_ID) VALUES(1, DATE '1970-01-01','Hans',TRUE,'Meier',1, 1);
INSERT INTO RESIDENT (PERSON_ID, DATE_OF_BIRTH, FIRST_NAME, MOVE_ALLOWED, OFFICIAL_NAME, SEX, HOUSEHOLD_ID) VALUES(2, DATE '1980-01-01','Ruth',FALSE,'Meier',2, NULL);
INSERT INTO RESIDENT (PERSON_ID, DATE_OF_BIRTH, FIRST_NAME, MOVE_ALLOWED, OFFICIAL_NAME, SEX, HOUSEHOLD_ID) VALUES(3, DATE '1982-01-01','Anna',TRUE,'Meier',2, 1);
INSERT INTO RESIDENT (PERSON_ID, DATE_OF_BIRTH, FIRST_NAME, MOVE_ALLOWED, OFFICIAL_NAME, SEX, HOUSEHOLD_ID) VALUES(4, DATE '2001-01-01','Fritz',TRUE,'Meier',1, 1);
INSERT INTO RESIDENT (PERSON_ID, DATE_OF_BIRTH, FIRST_NAME, MOVE_ALLOWED, OFFICIAL_NAME, SEX, HOUSEHOLD_ID) VALUES(5, DATE '2011-01-01','Anneliesi',TRUE,'Meier',2, 1);
INSERT INTO RESIDENT (PERSON_ID, DATE_OF_BIRTH, FIRST_NAME, MOVE_ALLOWED, OFFICIAL_NAME, SEX, HOUSEHOLD_ID) VALUES(6, DATE '1960-01-01','Pipi',TRUE,'Langstrumpf',2, NULL);