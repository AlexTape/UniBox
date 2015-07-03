SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

CREATE SCHEMA IF NOT EXISTS `unibox` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `unibox`;

-- -----------------------------------------------------
-- Table `unibox`.`player`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unibox`.`player` ;

CREATE TABLE IF NOT EXISTS `unibox`.`player` (
  `PlayerID` INT NOT NULL AUTO_INCREMENT,
  `AdminRights` TINYINT(1) NOT NULL DEFAULT 0,
  `Name` VARCHAR(45) NOT NULL,
  `Password` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`PlayerID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `unibox`.`category`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unibox`.`category` ;

CREATE TABLE IF NOT EXISTS `unibox`.`category` (
  `CatID` INT NOT NULL AUTO_INCREMENT,
  `GameTitle` VARCHAR(45) NOT NULL,
  `NumberOfPlayers` INT NOT NULL,
  PRIMARY KEY (`CatID`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `unibox`.`game`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unibox`.`game` ;

CREATE TABLE IF NOT EXISTS `unibox`.`game` (
  `GameID` INT NOT NULL AUTO_INCREMENT,
  `GameName` VARCHAR(45) NOT NULL,
  `CatID` INT NOT NULL,
  PRIMARY KEY (`GameID`),
  INDEX `FKGameCategory_idx` (`CatID` ASC),
  CONSTRAINT `FKGameCategory`
    FOREIGN KEY (`CatID`)
    REFERENCES `unibox`.`category` (`CatID`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `unibox`.`result`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `unibox`.`result` ;

CREATE TABLE IF NOT EXISTS `unibox`.`result` (
  `ResultID` INT NOT NULL AUTO_INCREMENT,
  `GameID` INT NOT NULL,
  `PlayerID` INT NOT NULL,
  `Scoring` INT NOT NULL,
  PRIMARY KEY (`ResultID`),
  INDEX `FKResultPlayer_idx` (`PlayerID` ASC),
  INDEX `FKResultGame_idx` (`GameID` ASC),
  CONSTRAINT `FKResultPlayer`
    FOREIGN KEY (`PlayerID`)
    REFERENCES `unibox`.`player` (`PlayerID`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `FKResultGame`
    FOREIGN KEY (`GameID`)
    REFERENCES `unibox`.`game` (`GameID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;