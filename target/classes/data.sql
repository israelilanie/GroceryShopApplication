-- Insert Grocery Items
INSERT INTO GROCERY_ITEMS (ID, NAME, UNIT_PRICE, CATEGORY, DESCRIPTION) VALUES
                                                                            (1, 'Apple', 1.50, 'Fruits', 'Fresh red apples'),
                                                                            (2, 'Banana', 0.80, 'Fruits', 'Ripe yellow bananas'),
                                                                            (3, 'Milk', 2.50, 'Dairy', 'Fresh whole milk'),
                                                                            (4, 'Bread', 1.20, 'Bakery', 'Whole wheat bread'),
                                                                            (5, 'Chicken Breast', 5.99, 'Meat', 'Fresh chicken breast'),
                                                                            (6, 'Tomato', 1.00, 'Vegetables', 'Fresh tomatoes'),
                                                                            (7, 'Carrot', 0.70, 'Vegetables', 'Organic carrots'),
                                                                            (8, 'Eggs', 3.50, 'Dairy', 'Free range eggs (12 pack)'),
                                                                            (9, 'Rice', 4.00, 'Grains', 'Basmati rice 1kg'),
                                                                            (10, 'Orange Juice', 3.20, 'Beverages', 'Fresh orange juice 1L');

-- Insert Inventory
INSERT INTO INVENTORY (ID, GROCERY_ITEM_ID, STOCK_QUANTITY, LAST_UPDATED) VALUES
                                                                              (1, 1, 100, CURRENT_TIMESTAMP),
                                                                              (2, 2, 150, CURRENT_TIMESTAMP),
                                                                              (3, 3, 50, CURRENT_TIMESTAMP),
                                                                              (4, 4, 75, CURRENT_TIMESTAMP),
                                                                              (5, 5, 30, CURRENT_TIMESTAMP),
                                                                              (6, 6, 80, CURRENT_TIMESTAMP),
                                                                              (7, 7, 60, CURRENT_TIMESTAMP),
                                                                              (8, 8, 40, CURRENT_TIMESTAMP),
                                                                              (9, 9, 25, CURRENT_TIMESTAMP),
                                                                              (10, 10, 35, CURRENT_TIMESTAMP);