DROP DATABASE IF EXISTS GETTIFY_SERVER;

CREATE DATABASE GETTIFY_SERVER;

USE GETTIFY_SERVER;

-- product id, listing id and gtin should be unique

CREATE TABLE CATEGORIES (
	cat_id VARCHAR(254) NOT NULL,
	name VARCHAR(254) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE SUBCATEGORIES (
	cat_id VARCHAR(254) NOT NULL,
	subcat_id VARCHAR(254) NOT NULL,
	name VARCHAR(254) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE MICROCATEGORIES(
	mcat_id VARCHAR(254) NOT NULL,
	subcat_id VARCHAR(254) NOT NULL,
	name VARCHAR(254) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE LISTINGS (
	listing_id VARCHAR(254) NOT NULL,
	product_id VARCHAR(254) NOT NULL,
	internal_id VARCHAR(254),
	title TEXT NOT NULL,
	price FLOAT,
	shipping_price FLOAT,
	available TINYINT(1),
	currency_code VARCHAR(10),
	url_fetched VARCHAR(1023),
	product_condition VARCHAR(254),
	image_url VARCHAR(254),
	affiliate_id SMALLINT,
	time_expiring BIGINT,
	time_added BIGINT,
	KEY(`listing_id`),
	KEY(`product_id`),
	KEY(`internal_id`, `affiliate_id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE PRODUCTS (
	product_id VARCHAR(254) NOT NULL,
	mcat_id VARCHAR(254) NOT NULL,
	product_name TEXT NOT NULL,
	description BLOB,
	brand VARCHAR(254),
	gtin VARCHAR(254),
	gtin_type VARCHAR(254),
	image_url VARCHAR(254),
	time_added BIGINT,
	specs BLOB,
	KEY(`product_id`),
	KEY(`mcat_id`),
	KEY(`gtin`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

CREATE TABLE PRICETRENDS (
	listing_id VARCHAR(254),
	price FLOAT,
	shipping_price FLOAT,
	currency_code VARCHAR(10),
	available TINYINT(1),
	time_added BIGINT,
	KEY(`listing_id`),
	KEY(`price`),
	KEY(`time_added`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

create TABLE ProductUrlScrape
(
	id BIGINT NOT NULL AUTO_INCREMENT,
	product_uri VARCHAR(1024),
	time_updated BIGINT,
	merchant_id SMALLINT,
	KEY(`id`),
	KEY(`product_uri`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
