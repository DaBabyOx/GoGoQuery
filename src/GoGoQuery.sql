create database GoGoQuery;
use GoGoQuery;

create table MsUser(
    UserID int auto_increment primary key,
    UserDOB date not null,
    UserEmail varchar(255) not null,
    UserPassword varchar(255) not null,
    UserGender varchar(255) not null,
    UserRole varchar(255) not null);

create table MsItem(
    ItemID int auto_increment primary key,
    ItemName varchar(255) not null,
    ItemCategory varchar(255) not null,
    ItemPrice decimal(10,2) not null,
    ItemDesc varchar(255) not null,
    ItemStock int not null default 0);

create table MsCart(
    UserID int not null,
    ItemID int not null,
    Quantity int not null,
    foreign key (UserID) references MsUser(UserID),
    foreign key (ItemID) references  MsItem(ItemID),
    primary key (UserID, ItemID));

create table TransactionHeader(
  TransactionID int auto_increment primary key,
  UserID int not null,
  DateCreated date not null,
  Status varchar(255) not null,
  foreign key (UserID) references MsUser(UserID));

create table TransactionDetail(
    TransactionID int not null,
    ItemID int not null,
    Quantity int not null,
    foreign key (TransactionID) references TransactionHeader(TransactionID),
    foreign key (ItemID) references  MsItem(ItemID),
    primary key(TransactionID, ItemID));
