USE "Team2Proj"
GO


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
)


CREATE FUNCTION dbo.TrackRank
(@BookID INT)
RETURNS TABLE 
AS RETURN (
SELECT  b1.BookID ,b1.NAME , RANK() OVER( ORDER BY b2.BBEScore DESC) AS RankNum
FROM dbo.BOOKS b1 
JOIN dbo.BBEList b2
ON b1.BBEID  = b2.BBEID 
WHERE b1.BookID  = @BookID
)





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
)


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
)


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
)


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
)
