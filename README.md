# ToolRental POS
## Installation Instructions
1. download zip provided
2. extract zip contents to a directory on local machine
3. Ensure docker & compose cli are installed on evaluating machine.
4. Open terminal at the root of the extracted contents. ( You should see api, database, store, etc. . . )
5. Run docker compose up
6. Visit web browser and point to http://localhost:8081
7. If you see a successfully loaded webpage, You are installed!!

## Application Components
- In a browser for easy call triggering, use the frontend web application.
- For advanced database manipulations, use adminer at http://localhost:8084
- For raw API access use http://localhost:8082/{entity}
- For connecting to database in your own db client connect through http://localhost:5432

## Frontend
1. Visit http://localhost:8081
2. See welcome message and scroll or click to products section.
   3. Here you will see the products pulled from the API
4. Click 'Add to Cart' on a product to initiate a POST to /order-item 
5. Scroll down or click from menu to access your cart.
6. @todo: Use checkout button to clear the order / cart completely.

## API
POST /orders/
Responsible for persisting the order in database and validating the cart contents. In addition,  this call will return a HTML receipt for use in frontend application.

POST /calc-cart-price
Responsible for taking the cart contents, and providing a final cost applying all discounts and taking into account product daily charges.


## Testing
1. To test easily, open the project in Intelli J
2. All Test are located at api/src/test/.....
3. Run them to check for success. ( these also run during compilation)