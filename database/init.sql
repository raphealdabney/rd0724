DROP TABLE IF EXISTS "product";
CREATE TABLE "public"."product" (
                                    "id" integer NOT NULL,
                                    "name" character varying(255),
                                    "charges_daily" real,
                                    "charges_weekday" integer NOT NULL,
                                    "charges_weekend" integer NOT NULL,
                                    "charges_holiday" integer NOT NULL,
                                    "image" character varying(255),
                                    "tool_code" character varying(255),
                                    "tool_type" character varying(255),
                                    "brand" character varying(255),
                                    CONSTRAINT "product_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "product" ("id", "name", "charges_daily", "charges_weekday", "charges_weekend", "charges_holiday", "image", "tool_code", "tool_type", "brand") VALUES
                        (1,	'Chainsaw',	1.49, 1, 0,	1, 'stihl-chainsaw.jpeg', 'CHNS', 'chainsaw', 'stihl'),
                        (2,	'Ladder', 1.99, 1, 1, 0, 'werner-ladder.jpeg', 'LADW', 'ladder', 'werner'),
                        (3,	'DeWalt Jackhammer', 2.99, 1, 0, 0, 'dewalt-jackhammer.jpg', 'JAKD', 'jackhammer', 'dewalt' ),
                        (4,	'Ridgid Jackhammer', 2.99, 1, 0, 0, 'ridgid-jackhammer.jpg', 'JAKR', 'jackhammer', 'ridgid'	);

DROP TABLE IF EXISTS "shop_order";
DROP SEQUENCE IF EXISTS shop_order_id_seq;
CREATE SEQUENCE shop_order_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 8 CACHE 1;

CREATE TABLE "public"."shop_order" (
                                       "id" integer DEFAULT nextval('shop_order_id_seq') NOT NULL,
                                       "date" date,
                                       CONSTRAINT "shop_order_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

DROP TABLE IF EXISTS "shop_order_item";
DROP SEQUENCE IF EXISTS shop_order_item_id_seq;
CREATE SEQUENCE shop_order_item_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 8 CACHE 1;

CREATE TABLE "public"."shop_order_item" (
                                       "id" integer DEFAULT nextval('shop_order_item_id_seq') NOT NULL,
                                       "tool_code" character varying(255),
                                        "shop_order_id" integer,
                                        "rental_day_count" integer,
                                        "discount_percentage" integer,
                                       CONSTRAINT "shop_order_item_pkey" PRIMARY KEY ("id")
) WITH (oids = false);
