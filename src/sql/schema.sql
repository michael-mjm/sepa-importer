-- create database if not exist
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'SepaImportDB')
BEGIN
    CREATE DATABASE SepaImportDB;
END
GO

USE SepaImportDB;
GO

-- table for structure definitions
CREATE TABLE SepaStructureRecords (
                                      Id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                      ModificationType CHAR(1) NOT NULL,
                                      RecordKey VARCHAR(12) NOT NULL,
                                      RecordStructure VARCHAR(35) NOT NULL,
                                      RecordContentType CHAR(1) NOT NULL,
                                      RecordStatus CHAR(1) NOT NULL,
                                      StartDate DATE,
                                      StopDate DATE,
                                      Attribute1 VARCHAR(100),
                                      Attribute2 VARCHAR(100),
                                      Attribute3 VARCHAR(100),
                                      Attribute4 VARCHAR(100),
                                      Attribute5 VARCHAR(100),
                                      Attribute6 VARCHAR(1000),
                                      Attribute7 VARCHAR(20),
                                      Attribute8 VARCHAR(255),
                                      Attribute9 CHAR(1),
                                      Attribute10 CHAR(1),
                                      CreatedAt DATETIME2 DEFAULT GETDATE(),

                                      INDEX IX_RecordKey (RecordKey),
                                      INDEX IX_ModificationType (ModificationType),
                                      INDEX IX_RecordStatus (RecordStatus)
);
GO

-- Dead Letter Queue for failed Records
CREATE TABLE SepaDeadLetterQueue (
                                     Id BIGINT IDENTITY(1,1) PRIMARY KEY,
                                     RecordKey VARCHAR(12),
                                     ModificationType VARCHAR(10),
                                     RawData NVARCHAR(MAX),
                                     ErrorMessage NVARCHAR(1000),
                                     ErrorTimestamp DATETIME2 DEFAULT GETDATE(),
                                     RetryCount INT DEFAULT 0,
                                     Resolved BIT DEFAULT 0
);
GO

-- Import Log table
CREATE TABLE ImportLog (
                           LogId BIGINT IDENTITY(1,1) PRIMARY KEY,
                           FileName NVARCHAR(255),
                           ProcessedRows INT,
                           ErrorCount INT,
                           StartTime DATETIME2 DEFAULT GETDATE(),
                           EndTime DATETIME2,
                           Status NVARCHAR(50)
);
GO