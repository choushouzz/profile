USE "Team2Proj"
GO

CREATE  View BooksReport AS
SELECT * 
FROM dbo.BOOKS b 

CREATE  View AuthorReport AS
SELECT * 
FROM dbo.Authors a 

CREATE  View PublishersReport AS
SELECT * 
FROM dbo.Publishers

CREATE  View GenreReport AS
SELECT b.BookID ,b.NAME AS BookName, g.GenreID ,g.Name  AS GenreName
FROM dbo.Category c
JOIN dbo.BOOKS b 
ON c.BookID  = b.BookID 
JOIN  dbo.Genres g 
ON g.GenreID  = c.GenreID 


CREATE  View AwardsReport AS
SELECT g.AwardID , a.Name  AS AwardName, g.BookID ,b.NAME AS BookName , g.AwardGettingInfoID ,g.[Date] 
FROM dbo.AwardGettinginfo g 
JOIN dbo.Awards a
ON g.AwardID  = a.AwardID 
JOIN dbo.BOOKS b 
ON b.BookID  = g.BookID 

CREATE  View SeriesReport AS
SELECT b.BookID , b.NAME  AS BookName, b.SeriesID , s.Name AS SeriesName
FROM dbo.Series s 
JOIN dbo.BOOKS b
ON s.SeriesID = b.BookID  

CREATE  VIEW  PriceReport AS
SELECT  b.BookID , b.NAME  AS BookName, AVG(Price) AS AveragePrice
FROM Orders o 
JOIN BOOKS b 
ON b.BookID  = o.BookID 
GROUP BY b.BookID , b.NAME

CREATE VIEW BBE_Report AS
SELECT * 
FROM BBEList b 

CREATE VIEW DescriptionReport AS
SELECT b.BookID ,b.NAME AS BookName, b.Description 
FROM dbo.BOOKS b 

CREATE VIEW SellerReport AS
SELECT * 
FROM dbo.Sellers s 

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
ON c.CustomerID = o.CustomerID 


CREATE VIEW CustomerReport AS
SELECT * 
FROM dbo.Customers c 

