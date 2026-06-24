SET search_path TO polideportivo;

INSERT INTO disciplina(nombre,descripcion) VALUES
('Futbol 5','Futbol en equipos de 5 con canchas cortas de zacate sintetico'),
('Futbol 11','Futbol en equipos de 11 con canchas grandes de zacate natural'),
('Futbol Sala','Futbol en equipos de 5 con cancha corta de cemento'),
('Tenis','Deporte con raquetas en cancha de cemento'),
('Baloncesto','Deporte con tableros y aros en cancha de cemento'),
('Voleibol','Deporte con ned en cancha bajo techo'),
('Voleibol de Playa','Deporte con ned en cancha de arena'),
('Ping Pong','Tenis de mesa con raquetas'),
('Beisbol','Deporte con bate en cancha grande'),
('Natacion','Deporte en piscina olimpica'),
('Atletismo','Carreras y pruebas en pista'),
('Mini Golf','Golf en mini circuitos de varios hoyos con obstaculos'),
('Escalada','Deporte en muro de escalada'),
('Artes Marciales','Practicas como karate, judo o taekwondo'),
('Boxeo','Deporte de combate en ring');

INSERT INTO espacio(nombre,descripcion,id_disciplina,capacidad,hora_apertura,hora_cierre,precio_hora) VALUES
('Cancha Futbol 5 #1','Cancha sintetica para futbol 5',1,10,'08:00','22:00',15000),
('Cancha Futbol 5 #2','Cancha sintetica para futbol 5',1,10,'08:00','22:00',15000),
('Cancha Futbol 5 #3','Cancha sintetica para futbol 5',1,10,'08:00','22:00',15000),
('Cancha Futbol 5 #4','Cancha sintetica para futbol 5',1,10,'08:00','22:00',15000),

('Cancha Futbol 11 #1','Cancha natural para futbol 11',2,22,'08:00','20:00',28000),
('Cancha Futbol 11 #2','Cancha natural para futbol 11',2,22,'08:00','20:00',28000),
('Cancha Futbol 11 #3','Cancha natural para futbol 11',2,22,'08:00','20:00',28000),
('Cancha Futbol 11 #4','Cancha natural para futbol 11',2,22,'08:00','20:00',28000),

('Futbol Sala #1','Cancha de cemento para futbol sala',3,10,'08:00','22:00',15000),
('Futbol Sala #2','Cancha de cemento para futbol sala',3,10,'08:00','22:00',15000),
('Futbol Sala #3','Cancha de cemento para futbol sala',3,10,'08:00','22:00',15000),
('Futbol Sala #4','Cancha de cemento para futbol sala',3,10,'08:00','22:00',15000),

('Tenis #1','Cancha de tenis',4,4,'09:00','21:00',10000),
('Tenis #2','Cancha de tenis',4,4,'09:00','21:00',10000),
('Tenis #3','Cancha de tenis',4,4,'09:00','21:00',10000),

('Baloncesto #1','Cancha de baloncesto',5,10,'08:00','22:00',11000),
('Baloncesto #2','Cancha de baloncesto',5,10,'08:00','22:00',11000),
('Baloncesto #3','Cancha de baloncesto',5,10,'08:00','22:00',11000),

('Voleibol #1','Cancha de voleibol bajo techo',6,12,'08:00','22:00',13000),
('Voleibol #2','Cancha de voleibol bajo techo',6,12,'08:00','22:00',13000),
('Voleibol #3','Cancha de voleibol bajo techo',6,12,'08:00','22:00',13000),

('Voleibol Playa #1','Cancha de voleibol de playa',7,12,'08:00','20:00',14000),
('Voleibol Playa #2','Cancha de voleibol de playa',7,12,'08:00','20:00',14000),

('Mesa Ping Pong #1','Mesa de ping pong',8,4,'08:00','22:00',2500),
('Mesa Ping Pong #2','Mesa de ping pong',8,4,'08:00','22:00',2500),
('Mesa Ping Pong #3','Mesa de ping pong',8,4,'08:00','22:00',2500),
('Mesa Ping Pong #4','Mesa de ping pong',8,4,'08:00','22:00',2500),
('Mesa Ping Pong #5','Mesa de ping pong',8,4,'08:00','22:00',2500),
('Mesa Ping Pong #6','Mesa de ping pong',8,4,'08:00','22:00',2500),
('Mesa Ping Pong #7','Mesa de ping pong',8,4,'08:00','22:00',2500),
('Mesa Ping Pong #8','Mesa de ping pong',8,4,'08:00','22:00',2500),

('Beisbol #1','Campo de beisbol',9,24,'08:00','18:00',25000),

('Piscina Olimpica #1','Piscina olimpica',10,15,'06:00','20:00',12000),
('Piscina Olimpica #2','Piscina olimpica',10,15,'06:00','20:00',12000),

('Atletismo #1','Pista de atletismo',11,25,'06:00','20:00',15000),
('Atletismo #2','Pista de atletismo',11,25,'06:00','20:00',15000),

('Mini Golf #1','Circuito de mini golf',12,10,'09:00','21:00',7500),
('Mini Golf #2','Circuito de mini golf',12,10,'09:00','21:00',7500),
('Mini Golf #3','Circuito de mini golf',12,10,'09:00','21:00',7500),

('Muro Escalada #1','Muro de escalada',13,4,'09:00','21:00',9000),
('Muro Escalada #2','Muro de escalada',13,4,'09:00','21:00',9000),

('Artes Marciales #1','Sala de artes marciales',14,15,'08:00','21:00',10000),
('Artes Marciales #2','Sala de artes marciales',14,15,'08:00','21:00',10000),

('Boxeo #1','Ring de boxeo',15,8,'08:00','21:00',10000),
('Boxeo #2','Ring de boxeo',15,8,'08:00','21:00',10000);

INSERT INTO equipamiento(nombre,cantidad_total,id_disciplina) VALUES
('Balon Futbol 5',20,1),
('Chalecos Futbol 5',50,1),
('Balon Futbol 11',16,2),
('Chalecos Futbol 11',100,1),
('Balon Futbol Sala',12,3),
('Chalecos Futbol Sala',50,1),
('Raqueta Tenis',20,4),
('Pelotas Tenis',30,4),
('Balon Baloncesto',12,5),
('Balon Voleibol',12,6),
('Balon Voleibol Playa',8,7),
('Raqueta Ping Pong',32,8),
('Pelotas Ping Pong',100,8),
('Bates Beisbol',20,9),
('Pelotas Beisbol',50,9),
('Guantes Beisbol',25,9),
('Tablas Natacion',20,10),
('Conos Entrenamiento',50,11),
('Palos Mini Golf',30,12),
('Pelotas Mini Golf',50,12),
('Guantes Entrenamiento',30,14),
('Guantes Boxeo',20,15),
('Vendas Boxeo',30,15);