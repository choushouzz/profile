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
    BookID INT IDENTITY PRIMARY KEY,
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
     Name TEXT
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
    RatingByStars DECIMAL(2,1),
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
    Name TEXT
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
    AwardID INT IDENTITY PRIMARY KEY,
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
REFERENCES dbo.Series(SeriesID);
ALTER TABLE dbo.BOOKS ADD CONSTRAINT FK_BOOKS2 FOREIGN KEY(AuthorID) 
REFERENCES dbo.Authors(AuthorID);
ALTER TABLE dbo.BOOKS ADD CONSTRAINT FK_BOOKS3 FOREIGN KEY(PublisherID) 
REFERENCES dbo.Publishers(PublisherID);
ALTER TABLE dbo.BOOKS ADD CONSTRAINT FK_BOOKS4 FOREIGN KEY(BBEID) 
REFERENCES dbo.BBEList(BBEID);
ALTER TABLE dbo.Orders ADD CONSTRAINT FK_Orders1 FOREIGN KEY(BookID) 
REFERENCES dbo.BOOKS(BookID);
ALTER TABLE dbo.Orders ADD CONSTRAINT FK_Orders2 FOREIGN KEY(SellerID) 
REFERENCES dbo.Sellers(SellerID);
ALTER TABLE dbo.Orders ADD CONSTRAINT FK_Orders3 FOREIGN KEY(CustomerID) 
REFERENCES dbo.Customers(CustomerID);

--2 Insert our data
INSERT dbo.Customers
	VALUES('Sharoom','Rubi','1990-07-18','M'),
		  ('Mikkel','Echo','2005-10-20','F'),
		  ('Mio','Topu','1984-07-13','M'),
		  ('Dilan','Linghsiu','1991-12-03','F'),
		  ('Crystal','Sausan','2000-11-16','F'),
		  ('Samet','Adeel','1983-11-17','F'),
		  ('Selena','Roy','1985-05-25','M'),
		  ('Vikas','Juju','1991-03-03','M'),
		  ('Sidney','Mariel','1988-06-18','F'),
		  ('Natsuko','Ani','1981-07-09','F'),
		  ('Bonnie','Zachory','1990-12-19','M'),
		  ('Likowsky','Adishwar','1981-01-19','M');

INSERT dbo.BBEList
	VALUES(2993816,30516,6376780,4.4,96),
	      (2632233,26923,2507623,5.0,98),
	      (2269402,23328,4501075,4.2,95),
	      (1983116,20452,2998241,4.1,94),
	      (1459448,14874,4964519,3.2,78),
	      (1372809,14168,1834276,4.3,96),
	      (1276599,13264,2740713,4.0,91),
	      (1238556,12949,517740,4.6,96),
	      (1159802,12111,110146,4.9,98),
	      (1087732,11211,1074620,4.4,94),
	      (1087056,11287,3550714,4.2,93),
	      (1063601,10996,1436325,3.8,84);

INSERT dbo.Authors
	VALUES('Oksana','Augustine','1965-03-11','M'),
	      ('Ankur','Eugenio','1927-09-29','M'),
	      ('Argelis','Yurany','1961-11-04','F'),
	      ('Kamilah','Hien','1975-10-27','M'),
	      ('Kandisann','Maimi','1915-05-17','F'),
	      ('Mallory','Caryn','1974-04-07','F'),
	      ('Nam','Jimibeth','1933-04-08','F'),
	      ('Arjay','Chasity','1929-07-19','M'),
	      ('Ronn','Gennaro','1909-11-13','F'),
	      ('Deepti','Caterina','1973-05-29','M'),
	      ('NuAve','Chandtisse','1961-03-14','F'),
	      ('Isabelle','Sharon','1909-05-06','M');

INSERT dbo.Series
	VALUES('The Hunger Games #1'),
	      ('Harry Potter #5'),
	      ('To Kill a Mockingbird'),
	      ('The Twilight Saga #1'),
	      ('The Chronicles of Narnia (Publication Order) #1–7'),
	      ('The Lord of the Rings #0-3'),
	      ('The Hitchhikers Guide to the Galaxy #1'),
	      ('Robert Langdon #2'),
	      ('Alices Adventures in Wonderland #1-2'),
	      ('Divergent #1'),
	      ('The Mortal Instruments #1'),
	      ('Enders Saga #1'),
	      ('Anne of Green Gables #1'),
	      ('');

INSERT dbo.Sellers
	VALUES('Aspano','Luqman'),
	      ('Florencio','Ame'),
	      ('Ksenia','Nicola'),
	      ('Kamaran','Becca'),
	      ('Tenzin','Chantou'),
	      ('Sergio','Micheal'),
	      ('Pol','Mordy'),
	      ('Bo','Dipak'),
	      ('Leni','Yaasha'),
          ('Mirko','Emile');

INSERT dbo.Publishers
    VALUES('Scholastic Press'),
          ('Harper Perennial Modern Classics'),
          ('Modern Library'),
          ('Little Brown and Company'),
          ('Alfred A. Knopf'),
          ('Signet Classics'),
          ('Warner Books'),
          ('Ballantine Books'),
          ('Simon & Schuster'),
          ('Katherine Tegen Books');

INSERT dbo.MainCharacters
    VALUES('Grete','Souheil',21,'F'),
          ('Sego','Hanley',61,'M'),
          ('Henry','Loreto',52,'F'),
          ('Femi','DeeDee',39,'F'),
          ('Lexx','Hermann',61,'M'),
          ('Nise','Giulio',25,'M'),
          ('Caprice','Betina',23,'F'),
          ('Carson','Resorts',18,'F'),
          ('Kai Chieh','Laura',25,'F'),
          ('Lyndell','Philipe',22,'F');

INSERT dbo.Genres
	VALUES('Fiction'),
		  ('Dystopia'),
		  ('Fantasy'),
		  ('Romance'),
		  ('Science'),
		  ('Teen'),
		  ('Adventure'),
		  ('Classics'),
		  ('Drama'),
		  ('Audiobook');

INSERT dbo.Awards
    VALUES('Locus Award Nominee for Best Young Adult Book'),
          ('Georgia Peach Book Award'),
          ('Buxtehuder Bulle'),
          ('Golden Duck Award for Young Adult'),
          ('Charlotte Award'),
          ('The Inky Awards Shortlist for Silver Inky'),
          ('Premi Protagonista Jove for Categoria'),
          ('Bram Stoker Award for Works for Young Readers'),
          ('Anthony Award for Young Adult'),
          ('Books I Loved Best Yearly BILBY Awards for Older Readers');

INSERT dbo.BOOKS
	VALUES('The Hunger Games','Hardcover','District 12','First Edition','English',9780440000000,374,'WINNING MEANS FAME AND FORTUNE.LOSING MEANS CERTAIN DEATH.THE HUNGER GAMES HAVE BEGUN. In the ruins of a place once known as North America lies the nation of Panem, a shining Capitol surrounded by twelve outlying districts.',
	2,5,9,2),
	      ('Harry Potter and the Order of the Phoenix','Paperback','Hogwarts School of Witchcraft and Wizardry','US Edition','English',9780440000000,870,'There is a door at the end of a silent corridor. And it is haunting Harry Pottter dreams. Why else would he be waking in the middle of the night, screaming in terror?',
	13,8,4,7),
	      ('To Kill a Mockingbird','Paperback','Maycomb','First Edition','English',10000000000000,324,'The unforgettable novel of a childhood in a sleepy Southern town and the crisis of conscience that rocked it, To Kill A Mockingbird became both an instant bestseller and a critical success when it was first published in 1960',
	1,12,6,7),
	      ('Pride and Prejudice','Paperback','Derbyshire','Modern Library Classics','English',9780679783268,279,'Jane Austen called this brilliant work her own darling child and its vivacious heroine, Elizabeth Bennet, as delightful a creature as ever appeared in print',
	13,1,1,2),
	      ('Twilight','Paperback','Forks','Phoenix','English',9780320000000,501,'Deeply seductive and extraordinarily suspenseful, Twilight is a love story with bite',
	9,11,8,3),
	      ('The Book Thief','Hardcover','Molching','First Edition','English',9780380000000,552,'An alternate cover edition can be found hereIt is 1939. Nazi Germany. The country is holding its breath.',
	3,2,3,9),
	      ('Animal Farm','Paperback','England','First Edition','English',9780450000000,141,'here is an Alternate Cover Edition for this edition of this book here.A farm is taken over by its overworked, mistreated animals.',
	2,8,3,5),
	      ('The Chronicles of Narnia','Paperback','London','First Edition','English',9780240000000,767,'Journeys to the end of the world, fantastic creatures, and epic battles between good and evilhat more could any reader ask for in one book?',
	14,3,8,3),
	      ('The Hobbit and The Lord of the Rings','Paperback','Middle-earth','First Edition','English',9780350000000,1728,'This four-volume, boxed set contains J.R.R. Tolkiens epic masterworks The Hobbit and the three volumes of The Lord of the Rings',
	13,5,4,6),
	      ('The Fault in Our Stars','Hardcover','Indiana','First Edition','English',9780579783268,786,'Despite the tumor-shrinking medical miracle that has bought her a few years, Hazel has never been anything but terminal, her final chapter inscribed upon diagnosis.',
	10,9,4,2),
	      ('The Hitchhikers Guide to the Galaxy','Paperback','London','Second Edition','English',9780670000000,908,'Seconds before the Earth is demolished to make way for a galactic freeway, Arthur Dent is plucked off the planet by his friend Ford Prefect',
	13,3,1,5),
	      ('The Giving Tree','Hardcover','London','Second Edition','English',9780370000000,342,'Once there was a tree...and she loved a little boy',
	5,3,3,9),
	      ('Wuthering Heights','Paperback','England','Third Edition','English',9780390000000,732,'You can find the redesigned cover of this edition HERE.This best-selling Norton Critical Edition is based on the 1847 first edition of the novel. For the Fourth Edition, the editor has collated the 1847 text with several modern editions and has corrected a number of variants, including accidentals.',
	9,1,5,7),
	      ('The Da Vinci Code','Paperback','Paris','First Edition','France',9780307277671,490,'The elderly curator of the Louvre has been murdered inside the museum, his body covered in baffling symbols. As Langdon and gifted French cryptologist Sophie Neveu sort through the bizarre riddles, they are stunned to discover a trail of clues hidden in the works of Leonardo',
	5,2,10,11);

INSERT dbo.Orders 
	VALUES(5.09,14,8,3),
	      (7.38,6,4,9),
	      (10.32,2,8,1),
	      (10.32,9,2,8),
	      (2.1,4,9,5),
	      (3.8,7,7,4),
	      (4.42,10,1,5),
	      (10.32,1,5,3),
	      (4.87,14,10,2),
	      (6.79,3,9,4),
	      (13.22,8,5,12),
	      (18.85,11,6,1),
	      (6.29,5,1,8),
	      (5.75,12,6,11),
	      (4.01,13,3,9);

INSERT dbo.Category
	VALUES(3,1),
		  (4,6),
		  (2,2),
		  (13,8),
		  (1,1),
		  (3,6),
		  (13,5),
		  (7,6),
		  (1,7),
		  (7,1),
		  (12,4),
		  (1,3),
		  (13,9),
		  (5,10),
		  (14,8),
		  (11,9);

INSERT dbo.AwardGettingInfo
	VALUES('2009-07-10',5,10),
		  ('1984-08-05',3,5),
		  ('1987-04-09',9,3),
		  ('1983-08-11',12,10),
		  ('2015-10-03',10,1),
		  ('2019-03-08',10,8),
		  ('2006-09-09',6,1),
		  ('2008-07-01',8,2),
		  ('2010-07-25',1,5),
		  ('1999-06-17',1,6),
		  ('1985-10-06',9,6),
		  ('1980-03-24',4,5),
		  ('2013-10-31',3,3),
		  ('1980-12-29',11,1),
		  ('2017-04-06',11,4),
		  ('2001-10-19',7,9);

INSERT dbo.BooksHasMainCharacters
	VALUES(6,4),
          (2,9),
          (9,1),
          (12,8),
          (4,8),
          (11,8),
          (12,2),
          (7,9),
          (7,7),
          (1,9),
          (8,1),
          (14,5),
          (7,10),
          (3,9),
          (6,5),
          (9,6);

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
SELECT  b.BookID , CAST(b.NAME AS NVARCHAR(100)) AS BookName, AVG(Price) AS AveragePrice
FROM Orders o 
JOIN BOOKS b 
ON b.BookID  = o.BookID 
GROUP BY b.BookID , CAST(b.NAME AS NVARCHAR(100));

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


/* A book can have at most 5 genres */
CREATE FUNCTION dbo.CheckGenreNum(@BookID INT) RETURNS INT
BEGIN 
DECLARE @num int =(
SELECT COUNT(*)
FROM dbo.Category
WHERE BookID  = @BookID
)
SET @num = isnull(@num,0)
return @num
END

ALTER TABLE dbo.Category ADD CONSTRAINT Checkgenre CHECK(dbo.CheckGenreNum(BookID) <= 5)

/* A book has no sale can not be on BBE list */
CREATE FUNCTION dbo.CheckTotalSale(@BookID INT) RETURNS INT
BEGIN 
DECLARE @num int =(
SELECT BBEID 
FROM dbo.Books
WHERE BookID  = @BookID AND TotalSale  = 0
)
return @num
END

ALTER TABLE dbo.BOOKS ADD CONSTRAINT CheckSale CHECK(dbo.CheckTotalSale(BookID) IS NULL)

/* A book with user rating less than 3.0 can not get a reward */
CREATE FUNCTION dbo.CheckRating(@BookID INT) RETURNS float
BEGIN 
DECLARE @num float = (
SELECT UserRating 
FROM dbo.Books
WHERE BookID  = @BookID 
)
SET @num = isnull(@num,0)
return @num
END

ALTER TABLE dbo.AwardGettingInfo ADD CONSTRAINT CheckRate CHECK( dbo.CheckRating(BookID) > 3)

--5 FUNCTION:Computed Colums

/* Add a computed column to track total order for a book */
CREATE FUNCTION dbo.TrackTotalOrders
(@BookID INT)
RETURNS int
AS  
BEGIN 
DECLARE @total int =
(
SELECT  COUNT(o.OrderID) AS TotalOrder
FROM dbo.Orders o 
JOIN dbo.Books b
ON o.BookID  = b.BookID 
WHERE o.BookID = @BookID
GROUP BY b.BookID
)
SET @total = ISNULL(@total, 0);
RETURN @total;
END
/* test */
SELECT dbo.TrackTotalOrders(3);

ALTER TABLE dbo.BOOKS
ADD TotalSale AS dbo.TrackTotalOrders(BookID);


/* Add a computed column to rank of bbe score for a book */
CREATE FUNCTION dbo.TrackRank
(@BookID INT)
RETURNS INT
AS 
BEGIN
DECLARE @ranknum INT =
(
SELECT RankNum
FROM
(SELECT b1.BookID ,RANK() OVER( ORDER BY b2.BBEScore DESC) AS RankNum
FROM dbo.BOOKS b1 
JOIN dbo.BBEList b2
ON b1.BBEID  = b2.BBEID ) temp
WHERE BookID  = @BookID
)
SET @ranknum = isnull(@ranknum,0)
return @ranknum
END
/*test */
SELECT dbo.TrackRank(3);

ALTER TABLE dbo.BOOKS
ADD BBERank AS dbo.TrackRank(BookID);


/* Add a computed column to track how many orders of books made for each seller*/

CREATE FUNCTION dbo.TrackBookSold
(@SellerID INT)
RETURNS INT
AS 
BEGIN 
DECLARE @total int =
(
SELECT   COUNT(o.OrderID) AS TotalBooksSold
FROM dbo.Orders o 
JOIN dbo.Sellers s
ON o.SellerID = s.SellerID 
WHERE o.SellerID  = @SellerID
GROUP BY s.SellerID ,s.FirstName ,s.LastName 
)
SET @total = ISNULL(@total,0)
RETURN @total
END 
/* Test */
SELECT dbo.TrackBookSold(4);

ALTER TABLE dbo.Sellers 
ADD BookSold AS dbo.TrackBookSold(SellerID);


/* Add computed column to track user rating for each book */
CREATE FUNCTION dbo.TrackRating
(@BookID INT)
RETURNS float
AS 
BEGIN 
DECLARE @rate float =(
SELECT bb.RatingByStars
FROM dbo.BOOKS b 
JOIN dbo.BBEList bb
ON b.BBEID  = bb.BBEID 
WHERE b.BookID  = @BookID
)
SET @rate = ISNULL(@rate,0)
RETURN @rate
END 

/* test */
SELECT dbo.TrackRating(3);

ALTER TABLE dbo.BOOKS 
ADD UserRating AS dbo.TrackRating(BookID);

/* Add a computed column to track the ratio of each genres among all books for each genre */
CREATE FUNCTION dbo.TrackGenreRatio
(@GenreID INT)
RETURNS float 
AS
BEGIN 
DECLARE @RATIO float = (
SELECT (CAST(Count(c.BookID) AS DECIMAL(8,3)) / CAST((SELECT COUNT(*)FROM dbo.Category) AS DECIMAL(8,3))) AS GenreRatio
FROM dbo.Category c 
JOIN dbo.Genres g 
ON c.GenreID  = g.GenreID
WHERE c.GenreID  = @GenreID
GROUP BY g.GenreID ,g.Name 
)
SET @RATIO = ISNULL(@RATIO,0)
RETURN @RATIO
END 

/* test */
SELECT dbo.TrackGenreRatio(4);

ALTER TABLE dbo.Genres 
ADD GenreRatio AS dbo.TrackGenreRatio(GenreID);

/* Add a computed column to track age of customers and authors */
ALTER TABLE dbo.Customers 
ADD Age AS DATEDIFF(hour,DateOfBirth,GETDATE())/8766;

ALTER TABLE dbo.Authors 
ADD Age AS DATEDIFF(hour,DateOfBirth,GETDATE())/8766;

/* Add a computed column to track age range of customers*/
CREATE FUNCTION dbo.TrackAgeRange(@Birth date)
RETURNS varchar(50)
AS 
BEGIN 
DECLARE @AgeRange varchar(50) = ( 
SELECT CASE  WHEN  Age  <= 18 THEN '<= 18'
       WHEN Age between 19 and 29 THEN '19 -- 29'
       WHEN  Age  between 30 and 40 THEN '30 -- 40'
       WHEN Age  between 41 and 50 THEN '41 -- 50'
       WHEN  Age   between 51 and 60 THEN '51 -- 60'
       ELSE  '> 60' END AS age
FROM dbo.Customers c 
WHERE DateOfBirth  = @birth) 
RETURN @AgeRange
END 
/* test */
SELECT dbo.TrackAgeRange('2005-10-20');

ALTER TABLE dbo.Customers 
ADD AgeRange AS dbo.TrackAgeRange(DateOfBirth);

/* Add a computed column to track age ratio of each cusotmer among all customers */
CREATE FUNCTION dbo.TrackAgeRatio(@Birth date)
RETURNS FLOAT
AS 
BEGIN 
DECLARE @RATIO float = (
SELECT  CAST(Count(CustomerID) AS DECIMAL(8,3)) / CAST((SELECT COUNT(*)FROM dbo.Customers) AS DECIMAL(8,3))AS AgeRatio 
FROM dbo.Customers c  
WHERE AgeRange  = dbo.TrackAgeRange(@Birth)
GROUP BY AgeRange
)
SET @RATIO = ISNULL(@RATIO,0)
RETURN @RATIO
END 
/* test */
SELECT dbo.TrackAgeRatio('2005-10-20');

ALTER TABLE dbo.Customers 
ADD AgeRatio AS dbo.TrackAgeRatio(DateOfBirth);