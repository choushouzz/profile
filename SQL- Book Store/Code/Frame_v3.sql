CREATE DATABASE "Team2Proj"

USE "Team2Proj"
GO

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
    BookID INT PRIMARY KEY,
    NAME TEXT,
    BookFormat TEXT,
    Setting TEXT,
    Edition TEXT,
    Language VARCHAR(45),
    ISBN BIGINT,
    PageNum INT,
    Description TEXT,
    SeriesID INT,
    AuthorID INT,
    PublisherID INT,
    BBEID INT
);

CREATE TABLE Orders(
    OrderID INT IDENTITY PRIMARY KEY,
    Price DECIMAL(5,2),
    BookID INT,
    SellerID INT,
    CustomerID INT
);

CREATE TABLE Sellers(
    SellerID INT IDENTITY PRIMARY KEY,
    FirstName VARCHAR(45),
    LastName VARCHAR(45)
);

CREATE TABLE Customers(
    CustomerID INT IDENTITY PRIMARY KEY,
    FirstName VARCHAR(45),
    LastName VARCHAR(45),
    DateOfBirth DATE,
    Sex VARCHAR(10)
);

CREATE TABLE Publishers(
     PublisherID INT IDENTITY PRIMARY KEY,
     Name VARCHAR(200)
);

CREATE TABLE Authors(
    AuthorID INT IDENTITY PRIMARY KEY,
    FirstName VARCHAR(45),
    LastName VARCHAR(45),
    DateOfBirth DATE,
    Sex VARCHAR(10)
);

CREATE TABLE BBEList(
    BBEID INT IDENTITY PRIMARY KEY,
    BBEScore INT,
    BBEVote INT,
    unmRatings INT,
    RatingByStars VARCHAR(200),
    LikedPercent INT
);

CREATE TABLE MainCharacters(
    CharacterID INT IDENTITY PRIMARY KEY,
    FirstName VARCHAR(45),
    LastName VARCHAR(45),
    Age INT,
    Sex VARCHAR(10)
);

CREATE TABLE Series(
    SeriesID INT IDENTITY PRIMARY KEY,
    Name VARCHAR(200)
);

CREATE TABLE Genres(
    GenreID INT IDENTITY PRIMARY KEY,
    Name VARCHAR(200)
);

CREATE TABLE BooksHasMainCharacters(
    BookID INT 
    REFERENCES dbo.BOOKS(BookID),
    CharacterID INT 
    REFERENCES dbo.MainCharacters(CharacterID),
    CONSTRAINT PK_BHMC PRIMARY KEY CLUSTERED(BookID,CharacterID)
);

CREATE TABLE Category(
     BookID INT
     REFERENCES dbo.BOOKS(BookID),
     GenreID INT
     REFERENCES dbo.Genres(GenreID),
     CONSTRAINT PK_Catrgory PRIMARY KEY CLUSTERED(BookID,GenreID)
);

CREATE TABLE Awards(
    AwardID INT PRIMARY KEY,
    Name TEXT
);

CREATE TABLE AwardGettingInfo(
    AwardGettingInfoID INT IDENTITY PRIMARY KEY,
    Date DATE,
    BookID INT FOREIGN KEY
    REFERENCES dbo.BOOKS(BookID),
    AwardID INT FOREIGN KEY
    REFERENCES dbo.Awards(AwardID)
);

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
BULK INSERT Customers FROM '/Users/xuqidi/Desktop/Customers.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT BBEList FROM '/Users/xuqidi/Desktop/BBEList.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT Authors FROM '/Users/xuqidi/Desktop/Authors.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT Series FROM '/Users/xuqidi/Desktop/Series.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT Sellers FROM '/Users/xuqidi/Desktop/Sellers.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT Publishers FROM '/Users/xuqidi/Desktop/Publishers.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT Orders FROM '/Users/xuqidi/Desktop/Orders.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT MainCharacters FROM '/Users/xuqidi/Desktop/MainCharacters.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT Genres FROM '/Users/xuqidi/Desktop/Genres.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT BOOKS FROM '/Users/xuqidi/Desktop/Books.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT Category FROM '/Users/xuqidi/Desktop/Category.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT BooksHasMainCharacters FROM '/Users/xuqidi/Desktop/BooksHasMainCharacters.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT Awards FROM '/Users/xuqidi/Desktop/Awards.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

BULK INSERT AwardGettingInfo FROM '/Users/xuqidi/Desktop/AwardGettingInfo.csv' 
WITH (FIRSTROW = 2,FIELDTERMINATOR = ',' , ROWTERMINATOR = '\n');

--3 Create views for reporting
CREATE  View BooksReport AS
SELECT * 
FROM dbo.BOOKS b;

CREATE  View AuthorReport AS
SELECT * 
FROM dbo.Authors a; 

CREATE  View PublishersReport AS
SELECT * 
FROM dbo.Publishers;

CREATE  View GenreReport AS
SELECT b.BookID ,b.NAME AS BookName, g.GenreID ,g.Name  AS GenreName
FROM dbo.Category c
JOIN dbo.BOOKS b 
ON c.BookID  = b.BookID 
JOIN  dbo.Genres g 
ON g.GenreID  = c.GenreID; 

CREATE  View AwardsReport AS
SELECT g.AwardID , a.Name  AS AwardName, g.BookID ,b.NAME AS BookName , g.AwardGettingInfoID ,g.[Date] 
FROM dbo.AwardGettinginfo g 
JOIN dbo.Awards a
ON g.AwardID  = a.AwardID 
JOIN dbo.BOOKS b 
ON b.BookID  = g.BookID; 

CREATE  View SeriesReport AS
SELECT b.BookID , b.NAME  AS BookName, b.SeriesID , s.Name AS SeriesName
FROM dbo.Series s 
JOIN dbo.BOOKS b
ON s.SeriesID = b.BookID;  

CREATE  VIEW  PriceReport AS
SELECT  b.BookID , b.NAME  AS BookName, AVG(Price) AS AveragePrice
FROM Orders o 
JOIN BOOKS b 
ON b.BookID  = o.BookID 
GROUP BY b.BookID , b.NAME;

CREATE VIEW BBE_Report AS
SELECT * 
FROM BBEList b; 

CREATE VIEW DescriptionReport AS
SELECT b.BookID ,b.NAME AS BookName, b.Description 
FROM dbo.BOOKS b; 

CREATE VIEW SellerReport AS
SELECT * 
FROM dbo.Sellers s; 

CREATE VIEW OrdersReport AS
SELECT o.OrderID , o.BookID ,b.NAME  AS BookName, o.Price, s.SellerID , 
s.FirstName AS SellerFirst, s.LastName AS SellerLast, c.CustomerID ,
c.FirstName  AS CustomeFirst, c.LastName AS CustomerLast
FROM dbo.Orders o 
JOIN BOOKS b 
ON o.BookID  = b.BookID 
JOIN dbo.Sellers s 
ON s.SellerID  = o.SellerID 
JOIN dbo.Customers c 
ON c.CustomerID = o.CustomerID; 

CREATE VIEW CustomerReport AS
SELECT * 
FROM dbo.Customers c; 

--4 FUNCTION:Table-Level CHECK Constraints 

--5 FUNCTION:Computed Colums
CREATE FUNCTION dbo.TrackTotalOrders
(@BookID INT)
RETURNS TABLE
AS RETURN (
SELECT b.BookID , b.NAME  AS BookName, COUNT(o.OrderID) AS TotalOrder
FROM dbo.Orders o 
JOIN dbo.Books b
ON o.BookID  = b.BookID 
WHERE o.BookID = @BookID
GROUP BY b.BookID , b.NAME 
);

CREATE FUNCTION dbo.TrackRank
(@BookID INT)
RETURNS TABLE 
AS RETURN (
SELECT  b1.BookID ,b1.NAME , RANK() OVER( ORDER BY b2.BBEScore DESC) AS RankNum
FROM dbo.BOOKS b1 
JOIN dbo.BBEList b2
ON b1.BBEID  = b2.BBEID 
WHERE b1.BookID  = @BookID
);

CREATE FUNCTION dbo.TrackBookSold
(@SellerID INT)
RETURNS TABLE
AS RETURN (
SELECT  s.SellerID , s.FirstName , s.LastName , COUNT(o.OrderID) AS TotalBooksSold
FROM dbo.Orders o 
JOIN dbo.Sellers s
ON o.SellerID = s.SellerID 
WHERE o.SellerID  = @SellerID
GROUP BY s.SellerID ,s.FirstName ,s.LastName 
);

CREATE FUNCTION dbo.TrackRating
(@BookID INT)
RETURNS TABLE
AS RETURN (
SELECT b.BookID ,b.NAME  AS BookName, b.Edition , b.PublisherID ,bb.BBEID ,
bb.BBEScore ,bb.BBEVote ,bb.LikedPercent ,bb.RatingByStars ,bb.unmRatings 
FROM dbo.BOOKS b 
JOIN dbo.BBEList bb
ON b.BBEID  = bb.BBEID 
WHERE b.BookID  = @BookID
);

CREATE FUNCTION dbo.TrackGenreRatio
(@GenreID INT)
RETURNS TABLE
AS RETURN (
SELECT g.GenreID , g.Name , (CAST(Count(c.BookID) AS DECIMAL(8,3)) / CAST((SELECT COUNT(*)FROM dbo.Category) AS DECIMAL(8,3))) AS GenreRatio
FROM dbo.Category c 
JOIN dbo.Genres g 
ON c.GenreID  = g.GenreID
WHERE c.GenreID  = @GenreID
GROUP BY g.GenreID ,g.Name 
);

CREATE FUNCTION dbo.TrackAgeRatio()
RETURNS TABLE
AS RETURN(
SELECT TOP 100 PERCENT age, CAST(Count(CustomerID) AS DECIMAL(8,3)) / CAST((SELECT COUNT(*)FROM dbo.Customers) AS DECIMAL(8,3))AS AgeRatio FROM 
(
SELECT CASE  WHEN  DATEDIFF(hour,DateOfBirth,GETDATE())/8766  <= 18 THEN '<= 18'
       WHEN  DATEDIFF(hour,DateOfBirth,GETDATE())/8766  between 19 and 29 THEN '19 -- 29'
       WHEN  DATEDIFF(hour,DateOfBirth,GETDATE())/8766  between 30 and 40 THEN '30 -- 40'
        WHEN  DATEDIFF(hour,DateOfBirth,GETDATE())/8766  between 41 and 50 THEN '41 -- 50'
         WHEN  DATEDIFF(hour,DateOfBirth,GETDATE())/8766  between 51 and 60 THEN '51 -- 60'
        ELSE  '> 60' END AS age, c.CustomerID   , DATEDIFF(hour,DateOfBirth,GETDATE())/8766 AS ACTUAL
FROM dbo.Customers c ) temp
GROUP BY age
ORDER BY age
);

--6 Column Data Encryption

