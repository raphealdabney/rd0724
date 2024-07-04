DROP TABLE IF EXISTS "order_item";
DROP SEQUENCE IF EXISTS order_item_id_seq;
CREATE SEQUENCE order_item_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 7 CACHE 1;

CREATE TABLE "public"."order_item" (
                                       "id" integer DEFAULT nextval('order_item_id_seq') NOT NULL,
                                       "image_url" character varying(255),
                                       "order_id" integer,
                                       "price" character varying(255),
                                       "product_id" integer,
                                       "quantity" integer,
                                       CONSTRAINT "order_item_pkey" PRIMARY KEY ("id")
) WITH (oids = false);


DROP TABLE IF EXISTS "product";
CREATE TABLE "public"."product" (
                                    "id" integer NOT NULL,
                                    "name" character varying(255),
                                    "description" character varying(255),
                                    "image_url" character varying(255),
                                    "price" real,
                                    CONSTRAINT "product_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "product" ("id", "name", "description", "image_url", "price") VALUES
                                                                              (1,	'Windows',	'windows based machine',	'https://i.pcmag.com/imagery/reviews/00xBy0JjVybodfIwWxeGCkZ-1..v1679417407.jpg',	25),
                                                                              (2,	'Mac OS',	'Mac based machine',	'https://images.unsplash.com/photo-1514826786317-59744fe2a548?q=80&w=1000&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTR8fGFwcGxlJTIwbWFjfGVufDB8fDB8fHww',	30),
                                                                              (3,	'Ubuntu',	'Linux based machine',	'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSd6yvCeIVbHXOcwzN9CzDKNKwykpZ_lYDR7PewI66L7upLMaALB9OUX45FTdprea-xlTc&usqp=CAU',	15);

DROP TABLE IF EXISTS "shop_order";
DROP SEQUENCE IF EXISTS shop_order_id_seq;
CREATE SEQUENCE shop_order_id_seq INCREMENT 1 MINVALUE 1 MAXVALUE 2147483647 START 8 CACHE 1;

CREATE TABLE "public"."shop_order" (
                                       "id" integer DEFAULT nextval('shop_order_id_seq') NOT NULL,
                                       "date" date,
                                       "token" character varying(255),
                                       CONSTRAINT "shop_order_pkey" PRIMARY KEY ("id")
) WITH (oids = false);

INSERT INTO "shop_order" ("id", "date", "token") VALUES
    (8,	'2024-01-27',	'abcdefg');