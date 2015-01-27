ALTER TABLE package_structure ADD COLUMN filename text;
UPDATE package_structure SET filename = 'fourlaws.xml' WHERE package_id = '8c595138-da5d-48df-8120-682b0b927b1e';
UPDATE package_structure SET filename = 'kgp.xml' WHERE package_id = '7492127f-937a-4514-a8a7-98687ed1d6c7';
UPDATE package_structure SET filename = 'satisfied.xml' WHERE package_id = '2d57e091-0657-4c58-aac6-d42d79b92226';