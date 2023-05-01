--CREATE DATABASE "Team2Proj"

--USE "Team2Proj"
--GO

DROP TABLE IF EXISTS AwardGettingInfo
DROP TABLE IF EXISTS Awards
DROP TABLE IF EXISTS BooksHasMainCharacters
DROP TABLE IF EXISTS Category
DROP TABLE IF EXISTS BOOKS
DROP TABLE IF EXISTS Genres
DROP TABLE IF EXISTS MainCharacters
DROP TABLE IF EXISTS Orders
DROP TABLE IF EXISTS Publishers
DROP TABLE IF EXISTS Sellers
DROP TABLE IF EXISTS Series
DROP TABLE IF EXISTS Authors
DROP TABLE IF EXISTS BBEList
DROP TABLE IF EXISTS Customers

--1 Set up the basic Database
CREATE TABLE BOOKS(
    BookID INT IDENTITY PRIMARY KEY,
    NAME VARCHAR(45),
    BookFormat VARCHAR(45),
    Setting VARCHAR(45),
    Edition VARCHAR(45),
    Language VARCHAR(45),
    ISBN INT,
    PageNum INT,
    Description VARCHAR(200),
    SeriesID INT,
    AuthorID INT,
    PublisherID INT,
    BBEID INT
)


CREATE TABLE Orders(
    OrderID INT IDENTITY PRIMARY KEY,
    Price DECIMAL(10),
    BookID INT,
    SellerID INT,
    CustomerID INT
)


CREATE TABLE Sellers(
    SellerID INT IDENTITY PRIMARY KEY,
    FirstName VARCHAR(45),
    LastName VARCHAR(45)
)

CREATE TABLE Customers(
    CustomerID INT IDENTITY PRIMARY KEY,
    FirstName VARCHAR(45),
    LastName VARCHAR(45),
    DateOfBirth DATE,
    Sex VARCHAR(10)
)
 CREATE TABLE Publishers(
     PublisherID INT IDENTITY PRIMARY KEY,
     Name VARCHAR(45)
 )

CREATE TABLE Authors(
    AuthorID INT IDENTITY PRIMARY KEY,
    FirstName VARCHAR(45),
    LastName VARCHAR(45),
    DateOfBirth DATE,
    Sex VARCHAR(45)
)

CREATE TABLE BBEList(
    BBEID INT IDENTITY PRIMARY KEY,
    BBEScore INT,
    BBEVote INT,
    unmRatings INT,
    RatingByStars INT,
    LikedPercent INT
)

CREATE TABLE MainCharacters(
    CharacterID INT IDENTITY PRIMARY KEY,
    FirstName VARCHAR(45),
    LastName VARCHAR(45),
    Age INT,
    Sex VARCHAR(45)
)

CREATE TABLE Series(
    SeriesID INT IDENTITY PRIMARY KEY,
    Name VARCHAR(45)
)

CREATE TABLE Genres(
    GenreID INT IDENTITY PRIMARY KEY,
    Name VARCHAR(45)
)

CREATE TABLE BooksHasMainCharacters(
    BookID INT 
    REFERENCES dbo.BOOKS(BookID),
    CharacterID INT 
    REFERENCES dbo.MainCharacters(CharacterID),
    CONSTRAINT PK_BHMC PRIMARY KEY CLUSTERED(BookID,CharacterID)
)
 CREATE TABLE Category(
     BookID INT
     REFERENCES dbo.BOOKS(BookID),
     GenreID INT
     REFERENCES dbo.Genres(GenreID),
     CONSTRAINT PK_Catrgory PRIMARY KEY CLUSTERED(BookID,GenreID)
 )

CREATE TABLE Awards(
    AwardID INT IDENTITY PRIMARY KEY,
    Name VARCHAR(45)
)

CREATE TABLE AwardGettingInfo(
    AwardGettingInfoID INT IDENTITY PRIMARY KEY,
    Date DATE,
    BookID INT FOREIGN KEY
    REFERENCES dbo.BOOKS(BookID),
    AwardID INT FOREIGN KEY
    REFERENCES dbo.Awards(AwardID)
)

ALTER TABLE dbo.BOOKS ADD CONSTRAINT FK_BOOKS1 FOREIGN KEY(SeriesID) 
REFERENCES dbo.Series(SeriesID)
ALTER TABLE dbo.BOOKS ADD CONSTRAINT FK_BOOKS2 FOREIGN KEY(AuthorID) 
REFERENCES dbo.Authors(AuthorID)
ALTER TABLE dbo.BOOKS ADD CONSTRAINT FK_BOOKS3 FOREIGN KEY(PublisherID) 
REFERENCES dbo.Publishers(PublisherID)
ALTER TABLE dbo.BOOKS ADD CONSTRAINT FK_BOOKS4 FOREIGN KEY(BBEID) 
REFERENCES dbo.BBEList(BBEID)
ALTER TABLE dbo.Orders ADD CONSTRAINT FK_Orders1 FOREIGN KEY(BookID) 
REFERENCES dbo.BOOKS(BookID)
ALTER TABLE dbo.Orders ADD CONSTRAINT FK_Orders2 FOREIGN KEY(SellerID) 
REFERENCES dbo.Sellers(SellerID)
ALTER TABLE dbo.Orders ADD CONSTRAINT FK_Orders3 FOREIGN KEY(CustomerID) 
REFERENCES dbo.Customers(CustomerID)

--2 Insert our data from CSV files

--3 Create views for reporting

--4 FUNCTION:Table-Level CHECK Constraints 

--5 FUNCTION:Computed Colums 

--6 Column Data Encryption

