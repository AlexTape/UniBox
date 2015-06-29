-- -----------------------------------------------------
-- Data for table `unibox`.`category`
-- -----------------------------------------------------
START TRANSACTION;
USE `unibox`;
INSERT INTO `unibox`.`category` (`GameTitle`, `NumberOfPlayers`) VALUES ('Vier Gewinnt', 2);
INSERT INTO `unibox`.`category` (`GameTitle`, `NumberOfPlayers`) VALUES ('Tik Tak To', 2);
INSERT INTO `unibox`.`category` (`GameTitle`, `NumberOfPlayers`) VALUES ('Mensch aergere dich nicht', 4);
INSERT INTO `unibox`.`category` (`GameTitle`, `NumberOfPlayers`) VALUES ('Snake', 1);

COMMIT;

-- -----------------------------------------------------
-- Data for table `unibox`.`player` default Password = 'user' (encrypted)
-- -----------------------------------------------------
START TRANSACTION;
USE `unibox`;
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (1, 'Admin', '3022443b7e33a6a68756047e46b81bea');
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (0, 'user1', '3022443b7e33a6a68756047e46b81bea');
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (0, 'user2', '3022443b7e33a6a68756047e46b81bea');
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (0, 'user3', '3022443b7e33a6a68756047e46b81bea');
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (0, 'user4', '3022443b7e33a6a68756047e46b81bea');
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (0, 'user5', '3022443b7e33a6a68756047e46b81bea');
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (0, 'user6', '3022443b7e33a6a68756047e46b81bea');
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (0, 'user7', '3022443b7e33a6a68756047e46b81bea');
INSERT INTO `unibox`.`player` (`AdminRights`, `Name`, `Password`) VALUES (0, 'user8', '3022443b7e33a6a68756047e46b81bea');

COMMIT;

-- -----------------------------------------------------
-- Data for table `unibox`.`game`
-- -----------------------------------------------------
START TRANSACTION;
USE `unibox`;
INSERT INTO `unibox`.`game` (`GameName`, `CatID`) VALUES ('Peters Spiel', 2);
INSERT INTO `unibox`.`game` (`GameName`, `CatID`) VALUES ('Susis Spiel', 1);
INSERT INTO `unibox`.`game` (`GameName`, `CatID`) VALUES ('Franks Spiel', 2);
INSERT INTO `unibox`.`game` (`GameName`, `CatID`) VALUES ('Danas Spiel', 4);

COMMIT;

-- -----------------------------------------------------
-- Data for table `unibox`.`result`
-- -----------------------------------------------------
START TRANSACTION;
USE `unibox`;
INSERT INTO `unibox`.`result` (`GameID`, `PlayerID`, `Scoring`) VALUES (1, 2, 1);
INSERT INTO `unibox`.`result` (`GameID`, `PlayerID`, `Scoring`) VALUES (1, 3, 2);
INSERT INTO `unibox`.`result` (`GameID`, `PlayerID`, `Scoring`) VALUES (2, 4, 3);
INSERT INTO `unibox`.`result` (`GameID`, `PlayerID`, `Scoring`) VALUES (2, 5, 3);
INSERT INTO `unibox`.`result` (`GameID`, `PlayerID`, `Scoring`) VALUES (3, 6, 1);
INSERT INTO `unibox`.`result` (`GameID`, `PlayerID`, `Scoring`) VALUES (3, 2, 0);
INSERT INTO `unibox`.`result` (`GameID`, `PlayerID`, `Scoring`) VALUES (4, 5, 1);

COMMIT;