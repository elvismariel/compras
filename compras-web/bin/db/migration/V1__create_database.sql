-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema compras
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema compras
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `compras` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `compras` ;

-- -----------------------------------------------------
-- Table `compras`.`user`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `compras`.`user` (
  `user_name` VARCHAR(100) NOT NULL,
  `user_email` VARCHAR(100) NOT NULL,
  `user_phone` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`user_phone`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `compras`.`shopping`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `compras`.`shopping` (
  `shopping_id` INT NOT NULL,
  `shopping_name` VARCHAR(45) NOT NULL,
  `shopping_status` INT NULL,
  `user_phone` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`shopping_id`, `user_phone`),
  INDEX `fk_shopping_user1_idx` (`user_phone` ASC),
  CONSTRAINT `fk_shopping_user1`
    FOREIGN KEY (`user_phone`)
    REFERENCES `compras`.`user` (`user_phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `compras`.`product`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `compras`.`product` (
  `product_id` INT NOT NULL,
  `product_name` VARCHAR(45) NOT NULL,
  `product_status` INT NOT NULL,
  `product_department` INT NOT NULL,
  `product_user_phone` VARCHAR(45) NOT NULL,
  `shopping_id` INT NOT NULL,
  `shopping_user_phone` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`product_id`, `product_user_phone`, `shopping_id`, `shopping_user_phone`),
  INDEX `fk_product_user1_idx` (`product_user_phone` ASC),
  INDEX `fk_product_shopping1_idx` (`shopping_id` ASC, `shopping_user_phone` ASC),
  CONSTRAINT `fk_product_user1`
    FOREIGN KEY (`product_user_phone`)
    REFERENCES `compras`.`user` (`user_phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_product_shopping1`
    FOREIGN KEY (`shopping_id` , `shopping_user_phone`)
    REFERENCES `compras`.`shopping` (`shopping_id` , `user_phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `compras`.`synchronize`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `compras`.`synchronize` (
  `synchronize_id` VARCHAR(255) NOT NULL,
  `item_type` INT NOT NULL,
  `item_action` INT NOT NULL,
  `item_object` VARCHAR(1000) NOT NULL,
  `user_phone` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`synchronize_id`, `user_phone`),
  INDEX `fk_synchronize_user1_idx` (`user_phone` ASC),
  CONSTRAINT `fk_synchronize_user1`
    FOREIGN KEY (`user_phone`)
    REFERENCES `compras`.`user` (`user_phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `compras`.`share`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `compras`.`share` (
  `shopping_id` INT NOT NULL,
  `shopping_user_phone` VARCHAR(45) NOT NULL,
  `user_phone` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`shopping_id`, `shopping_user_phone`, `user_phone`),
  INDEX `fk_shopping_has_user_user1_idx` (`user_phone` ASC),
  INDEX `fk_shopping_has_user_shopping1_idx` (`shopping_id` ASC, `shopping_user_phone` ASC),
  CONSTRAINT `fk_shopping_has_user_shopping1`
    FOREIGN KEY (`shopping_id` , `shopping_user_phone`)
    REFERENCES `compras`.`shopping` (`shopping_id` , `user_phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_shopping_has_user_user1`
    FOREIGN KEY (`user_phone`)
    REFERENCES `compras`.`user` (`user_phone`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
