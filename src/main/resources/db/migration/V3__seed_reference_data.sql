-- V3 reference-data seed (Milestone 2.2).
-- Mirrors the frontend mock fixtures (ids, names, prices) one-for-one so the
-- SPA behaves identically once it swaps mock handlers for the real API.

-- Locations ------------------------------------------------------------------
INSERT INTO location (id, name, slug, category, city, country, lat, lng, active, sort_order) VALUES
    ('loc-ohd',          'Ohrid St. Paul the Apostle Airport', 'ohrid-airport',        'AIRPORT', 'Ohrid',        'North Macedonia', 41.18, 20.74, TRUE, 1),
    ('loc-skp-air',      'Skopje International Airport',        'skopje-airport',       'AIRPORT', 'Skopje',       'North Macedonia', 41.96, 21.62, TRUE, 2),
    ('loc-tia-air',      'Tirana International Airport',        'tirana-airport',       'AIRPORT', 'Tirana',       'Albania',         41.41, 19.71, TRUE, 3),
    ('loc-skg-air',      'Thessaloniki Airport',               'thessaloniki-airport', 'AIRPORT', 'Thessaloniki', 'Greece',          40.51, 22.97, TRUE, 4),
    ('loc-ohrid',        'Ohrid',                              'ohrid',                'CITY',    'Ohrid',        'North Macedonia', 41.11, 20.80, TRUE, 1),
    ('loc-skopje',       'Skopje',                             'skopje',               'CITY',    'Skopje',       'North Macedonia', 41.99, 21.43, TRUE, 2),
    ('loc-tirana',       'Tirana',                             'tirana',               'CITY',    'Tirana',       'Albania',         41.33, 19.82, TRUE, 3),
    ('loc-thessaloniki', 'Thessaloniki',                       'thessaloniki',         'CITY',    'Thessaloniki', 'Greece',          40.64, 22.94, TRUE, 4),
    ('loc-metropol',     'Metropol Hotel',                     'metropol-hotel',       'HOTEL',   'Ohrid',        'North Macedonia', 41.08, 20.79, TRUE, 1),
    ('loc-marriott',     'Marriott Hotel',                     'marriott-hotel',       'HOTEL',   'Skopje',       'North Macedonia', 41.99, 21.43, TRUE, 2),
    ('loc-granit',       'Hotel Granit Resort',                'granit-resort',        'RESORT',  'Ohrid',        'North Macedonia', 41.04, 20.79, TRUE, 1);

-- Vehicles -------------------------------------------------------------------
INSERT INTO vehicle (id, name, capacity, description, image_url, active, sort_order) VALUES
    ('veh-sedan',   'Economy Sedan',   3,  'Comfortable air-conditioned car, ideal for couples and solo travelers.', '/vehicles/sedan.jpg',   TRUE, 1),
    ('veh-minivan', 'Business Minivan', 6, 'Spacious minivan for families and small groups with extra luggage room.', '/vehicles/minivan.jpg', TRUE, 2),
    ('veh-van',     'Premium Van',     8,  'High-roof van with generous space, perfect for larger groups.',          '/vehicles/van.jpg',     TRUE, 3),
    ('veh-minibus', 'Minibus',         16, 'Comfortable minibus for tour groups and events.',                        '/vehicles/minibus.jpg', TRUE, 4);

INSERT INTO vehicle_feature (vehicle_id, position, label) VALUES
    ('veh-sedan',   0, 'Air conditioning'),
    ('veh-sedan',   1, 'Up to 3 luggage'),
    ('veh-sedan',   2, 'Free Wi-Fi'),
    ('veh-sedan',   3, 'Bottled water'),
    ('veh-minivan', 0, 'Air conditioning'),
    ('veh-minivan', 1, 'Up to 6 luggage'),
    ('veh-minivan', 2, 'Free Wi-Fi'),
    ('veh-minivan', 3, 'Child seat on request'),
    ('veh-van',     0, 'Air conditioning'),
    ('veh-van',     1, 'Up to 10 luggage'),
    ('veh-van',     2, 'Free Wi-Fi'),
    ('veh-van',     3, 'USB charging'),
    ('veh-minibus', 0, 'Air conditioning'),
    ('veh-minibus', 1, 'Large luggage hold'),
    ('veh-minibus', 2, 'Professional driver'),
    ('veh-minibus', 3, 'Reclining seats');

-- Transfer prices (From + To + Vehicle -> price), reverse routes seeded -------
INSERT INTO transfer_price (id, from_location_id, to_location_id, vehicle_id, price) VALUES
    ('tp-ohdair-ohrid-sedan',   'loc-ohd',     'loc-ohrid',          'veh-sedan',   25),
    ('tp-ohdair-ohrid-minivan', 'loc-ohd',     'loc-ohrid',          'veh-minivan', 40),
    ('tp-ohdair-ohrid-van',     'loc-ohd',     'loc-ohrid',          'veh-van',     70),
    ('tp-ohrid-skpair-sedan',   'loc-ohrid',   'loc-skp-air',        'veh-sedan',   120),
    ('tp-ohrid-skpair-minivan', 'loc-ohrid',   'loc-skp-air',        'veh-minivan', 150),
    ('tp-ohrid-skpair-van',     'loc-ohrid',   'loc-skp-air',        'veh-van',     190),
    ('tp-skpair-ohrid-sedan',   'loc-skp-air', 'loc-ohrid',          'veh-sedan',   120),
    ('tp-skpair-ohrid-minivan', 'loc-skp-air', 'loc-ohrid',          'veh-minivan', 150),
    ('tp-skpair-ohrid-van',     'loc-skp-air', 'loc-ohrid',          'veh-van',     190),
    ('tp-tiaair-ohrid-sedan',   'loc-tia-air', 'loc-ohrid',          'veh-sedan',   100),
    ('tp-tiaair-ohrid-minivan', 'loc-tia-air', 'loc-ohrid',          'veh-minivan', 130),
    ('tp-tiaair-ohrid-van',     'loc-tia-air', 'loc-ohrid',          'veh-van',     170),
    ('tp-ohrid-skg-minivan',    'loc-ohrid',   'loc-thessaloniki',   'veh-minivan', 180),
    ('tp-ohrid-skg-van',        'loc-ohrid',   'loc-thessaloniki',   'veh-van',     230),
    ('tp-ohrid-skg-minibus',    'loc-ohrid',   'loc-thessaloniki',   'veh-minibus', 320);

-- Testimonials ---------------------------------------------------------------
INSERT INTO testimonial (id, author_name, location, country, rating, content, created_at, published) VALUES
    ('tst-1',  'Sarah M.',   'London',       'United Kingdom',  5, 'Booked an airport transfer from Ohrid to Skopje. The driver was waiting for us, the car was spotless, and the price was exactly as quoted. Highly recommended!', '2026-05-12T09:30:00Z', TRUE),
    ('tst-2',  'Andreas K.', 'Thessaloniki', 'Greece',          5, 'Smooth cross-border trip from Thessaloniki to Ohrid. Great communication and a very professional driver.', '2026-04-28T14:10:00Z', TRUE),
    ('tst-3',  'Elira D.',   'Tirana',       'Albania',         4, 'Reliable pickup from Tirana Airport. Easy to book and good value for a private transfer.', '2026-06-02T18:45:00Z', TRUE),
    ('tst-4',  'Marco R.',   'Milan',        'Italy',           5, 'Perfect service for our family of five. The minivan was comfortable and the child seat we requested was ready. Will use again next summer.', '2026-06-18T07:20:00Z', TRUE),
    ('tst-5',  'Julia S.',   'Berlin',       'Germany',         5, 'Our flight was delayed by two hours but the driver tracked it and was still there waiting. That kind of reliability is priceless when travelling.', '2026-06-21T22:05:00Z', TRUE),
    ('tst-6',  'Nikola P.',  'Skopje',       'North Macedonia', 4, 'Good, punctual transfer from Skopje to Ohrid. Clean car and a friendly driver. Booking online was quick.', '2026-05-30T11:15:00Z', TRUE),
    ('tst-7',  'Emma T.',    'Amsterdam',    'Netherlands',     5, 'From the moment we booked to the drop-off at our hotel, everything was seamless. Fixed price, no surprises, lovely driver.', '2026-06-10T15:40:00Z', TRUE),
    ('tst-8',  'David L.',   'Manchester',   'United Kingdom',  5, 'Used them for a Tirana Airport to Ohrid transfer. Comfortable ride, great English, and they helped with our luggage. Five stars.', '2026-04-15T08:00:00Z', TRUE),
    ('tst-9',  'Sofia G.',   'Athens',       'Greece',          4, 'Very professional. The only small thing was a short wait at pickup, but the driver called ahead to let us know. Overall a great experience.', '2026-06-25T13:25:00Z', TRUE),
    ('tst-10', 'Thomas W.',  'Vienna',       'Austria',         5, 'Booked a return transfer for a business trip. Both drivers were on time and the vehicles were immaculate. Exactly what you want.', '2026-05-05T16:50:00Z', TRUE),
    ('tst-11', 'Ana M.',     'Belgrade',     'Serbia',          5, 'Great value and stress-free. We were a group of eight and the van fit everyone plus all the suitcases with ease.', '2026-06-28T10:10:00Z', TRUE),
    ('tst-12', 'Pierre B.',  'Lyon',         'France',          3, 'The transfer itself was fine and on time. Communication before the trip could have been a little faster, but no real complaints.', '2026-06-30T19:30:00Z', TRUE);
