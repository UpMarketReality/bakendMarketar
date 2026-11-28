-- Datos académicos. Contraseña de TODAS las cuentas: Demo12345!
-- Se ejecuta DESPUÉS de constraints.sql sobre el esquema nuevo de Hibernate.
INSERT INTO rol(idrol,nombre,descripcion) OVERRIDING SYSTEM VALUE VALUES
                                                                      (1,'VENDEDOR','Vendedor'),(2,'COMPRADOR','Comprador');
INSERT INTO usuario(usuarioid,nombre,correo,passwordhash,idrol) OVERRIDING SYSTEM VALUE VALUES
                                                                                            (1,'Comprador Uno','comprador1@marketar.test','$2b$12$XHa.C22MNkd2NiPDixaR3OpQHkLg/62E8TIwlqnja/L2uzkyzgaIC',2),
                                                                                            (2,'Comprador Dos','comprador2@marketar.test','$2b$12$XHa.C22MNkd2NiPDixaR3OpQHkLg/62E8TIwlqnja/L2uzkyzgaIC',2),
                                                                                            (3,'Vendedor Uno','vendedor1@marketar.test','$2b$12$XHa.C22MNkd2NiPDixaR3OpQHkLg/62E8TIwlqnja/L2uzkyzgaIC',1),
                                                                                            (4,'Vendedor Dos','vendedor2@marketar.test','$2b$12$XHa.C22MNkd2NiPDixaR3OpQHkLg/62E8TIwlqnja/L2uzkyzgaIC',1);
INSERT INTO comprador(idcomprador,usuarioid,direccion) OVERRIDING SYSTEM VALUE VALUES
                                                                                   (1,1,'Av. Demo 100, Lima'),(2,2,'Av. Demo 200, Lima');
INSERT INTO vendedor(idvendedor,usuarioid,nombretienda,descripciontienda,ruc) OVERRIDING SYSTEM VALUE VALUES
                                                                                                          (1,3,'Madera Demo','Mobiliario de demostración','20123456789'),
                                                                                                          (2,4,'Cerámica Demo','Cerámica de demostración','20987654321');
INSERT INTO categoriaproducto(idcategoria,nombre,descripcion,tipo) OVERRIDING SYSTEM VALUE VALUES
                                                                                               (1,'Muebles','Muebles','MOBILIARIO'),(2,'Cerámicas y tazas','Cerámica','CERAMICA'),(3,'Ropa y textiles','Textiles','TEXTIL');
INSERT INTO categoriaprototipo(idcategoriaprototipo,nombre,descripcion,tipo) OVERRIDING SYSTEM VALUE VALUES
                                                                                                         (1,'Muebles personalizados','Muebles','MOBILIARIO'),(2,'Cerámica personalizada','Cerámica','CERAMICA'),(3,'Textiles personalizados','Textiles','TEXTIL');
INSERT INTO producto(idproducto,nombreproducto,descripcion,preciounidad,dimensiones,material,color,stock,estado,idvendedor,idcategoria) OVERRIDING SYSTEM VALUE VALUES
                                                                                                                                                                    (1,'Mesa demo','Producto de prueba',100.00,'80x60x70 cm','Madera','Natural',8,'ACTIVO',1,1),
                                                                                                                                                                    (2,'Taza demo','Producto de prueba',150.00,'10x10x10 cm','Cerámica','Blanco',10,'ACTIVO',2,2),
                                                                                                                                                                    (3,'Silla agotada','Producto de prueba',90.00,'40x40x90 cm','Madera','Natural',0,'AGOTADO',1,1),
                                                                                                                                                                    (4,'Producto inactivo','No visible públicamente',80.00,'40x40x90 cm','Madera','Natural',4,'INACTIVO',1,1),
                                                                                                                                                                    (5,'Escritorio nórdico','Escritorio compacto con cajón lateral',320.00,'120x60x75 cm','Madera','Roble',6,'ACTIVO',1,1),
                                                                                                                                                                    (6,'Estante flotante','Repisa minimalista para pared',65.00,'60x20x4 cm','Madera','Nogal',15,'ACTIVO',1,1),
                                                                                                                                                                    (7,'Banco de barra','Banco alto con reposapiés',140.00,'35x35x75 cm','Madera','Natural',9,'ACTIVO',2,1),
                                                                                                                                                                    (8,'Mesa de noche','Velador con dos compartimentos',185.00,'45x40x55 cm','Madera','Blanco',7,'ACTIVO',1,1),
                                                                                                                                                                    (9,'Silla plegable','Silla práctica para espacios pequeños',75.00,'45x45x80 cm','Madera','Café',12,'ACTIVO',2,1),
                                                                                                                                                                    (10,'Librero modular','Estantería de cuatro niveles',290.00,'80x30x150 cm','Madera','Natural',4,'ACTIVO',1,1),
                                                                                                                                                                    (11,'Mesa de centro','Mesa baja para sala',210.00,'90x50x42 cm','Madera','Negro',0,'AGOTADO',1,1),
                                                                                                                                                                    (12,'Perchero de pie','Perchero vertical con 6 ganchos',95.00,'40x40x170 cm','Madera','Natural',11,'ACTIVO',2,1),
                                                                                                                                                                    (13,'Banca recibidor','Banca con zapatera inferior',240.00,'100x35x45 cm','Madera','Roble',3,'INACTIVO',1,1),
                                                                                                                                                                    (14,'Plato hondo artesanal','Plato para sopas y pastas',45.00,'22x22x5 cm','Cerámica','Beige',25,'ACTIVO',2,2),
                                                                                                                                                                    (15,'Juego de tazas espresso','Set de 4 tazas pequeñas',110.00,'7x7x6 cm','Cerámica','Negro',14,'ACTIVO',2,2),
                                                                                                                                                                    (16,'Jarra para agua','Jarra con asa ergonómica de 1.5L',85.00,'14x14x24 cm','Cerámica','Blanco',8,'ACTIVO',1,2),
                                                                                                                                                                    (17,'Maceta cilíndrica','Maceta con plato de drenaje incluido',55.00,'15x15x16 cm','Cerámica','Terracota',18,'ACTIVO',2,2),
                                                                                                                                                                    (18,'Bowl ensaladera','Recipiente amplio para ensaladas',70.00,'25x25x11 cm','Cerámica','Azul',10,'ACTIVO',1,2),
                                                                                                                                                                    (19,'Tetera clásica','Tetera con tapa y filtro interno',130.00,'18x14x16 cm','Cerámica','Verde',5,'ACTIVO',2,2),
                                                                                                                                                                    (20,'Florero minimalista','Florero decorativo de cuello estrecho',60.00,'10x10x25 cm','Cerámica','Gris',0,'AGOTADO',2,2),
                                                                                                                                                                    (21,'Azucarera con tapa','Recipiente pequeño con cucharita',38.00,'9x9x10 cm','Cerámica','Blanco',20,'ACTIVO',1,2);
INSERT INTO imagenproducto(idimagen,url,esprincipal,idproducto) OVERRIDING SYSTEM VALUE VALUES
                                                                                            (1,'/media/products/demo.png',true,1),
                                                                                            (2,'/media/products/producto-1',false,1),
                                                                                            (3,'/media/products/demo.png',true,2),
                                                                                            (4,'/media/products/demo.png',true,3),
                                                                                            (5,'/media/products/demo.png',true,4),
                                                                                            (6,'/media/products/producto-1.jpg',true,5),
                                                                                            (7,'/media/products/producto-2.jpg',true,6),
                                                                                            (8,'/media/products/producto-1.jpg',true,7),
                                                                                            (9,'/media/products/producto-2.jpg',true,8),
                                                                                            (10,'/media/products/producto-1.jpg',true,9),
                                                                                            (11,'/media/products/producto-2.jpg',true,10),
                                                                                            (12,'/media/products/producto-1.jpg',true,11),
                                                                                            (13,'/media/products/producto-2.jpg',true,12),
                                                                                            (14,'/media/products/producto-1.jpg',true,13),
                                                                                            (15,'/media/products/producto-2.jpg',true,14),
                                                                                            (16,'/media/products/producto-1.jpg',true,15),
                                                                                            (17,'/media/products/producto-2.jpg',true,16),
                                                                                            (18,'/media/products/producto-1.jpg',true,17),
                                                                                            (19,'/media/products/producto-2.jpg',true,18),
                                                                                            (20,'/media/products/producto-1.jpg',true,19),
                                                                                            (21,'/media/products/producto-2.jpg',true,20),
                                                                                            (22,'/media/products/producto-1.jpg',true,21);
INSERT INTO prototipoia(idprototipoia,nombreprototipo,prompt,especificaciones,modeloia,estadogeneracion,idcomprador,idcategoriaprototipo) OVERRIDING SYSTEM VALUE VALUES
                                                                                                                                                                      (1,'Mesa personalizada DEMO','Mesa de madera','{"material":"Madera","color":"Natural","dimensions":{"width":80,"height":70,"depth":60,"unit":"cm"}}','DATOS_DE_PRUEBA','COMPLETADO',1,1),
                                                                                                                                                                      (2,'Taza personalizada DEMO','Taza blanca','{"material":"Cerámica","color":"Blanco","capacityMl":300}','DATOS_DE_PRUEBA','COMPLETADO',1,2),
                                                                                                                                                                      (3,'Polo personalizado DEMO','Polo azul','{"material":"Algodón","color":"Azul","size":"M"}','DATOS_DE_PRUEBA','COMPLETADO',2,3);
INSERT INTO imagenprototipo(idimagen,url,esprincipal,idprototipoia) OVERRIDING SYSTEM VALUE VALUES
                                                                                                (1,'/api/v1/prototypes/1/images/00000000-0000-0000-0000-000000000001.png',true,1),
                                                                                                (2,'/api/v1/prototypes/2/images/00000000-0000-0000-0000-000000000002.png',true,2),
                                                                                                (3,'/api/v1/prototypes/3/images/00000000-0000-0000-0000-000000000003.png',true,3);
INSERT INTO solicitud(idsolicitud,estado,cantidad,direccionentrega,zonaentrega,condicionesentrega,especificacionesacordadas,imagenesreferencia,idcomprador,idprototipoia) OVERRIDING SYSTEM VALUE
SELECT p.idprototipoia,CASE p.idprototipoia WHEN 1 THEN 'COTIZADA' WHEN 2 THEN 'ACEPTADA' ELSE 'CANCELADA' END,2,'Av. Demo 100, Lima','Lima','Entrega coordinada incluida',p.especificaciones,jsonb_build_array(i.url),p.idcomprador,p.idprototipoia
FROM prototipoia p JOIN imagenprototipo i ON i.idprototipoia=p.idprototipoia AND i.esprincipal;
INSERT INTO cotizacionprototipo(idcotizacion,estado,comentario,moneda,fecha,fechavencimiento,monto,tiempoentrega,idsolicitud,idvendedor) OVERRIDING SYSTEM VALUE VALUES
                                                                                                                                                                     (1,'PENDIENTE','Incluye entrega','PEN',now()-interval '1 day',now()+interval '7 days',300,5,1,1),
                                                                                                                                                                     (2,'PENDIENTE','Incluye entrega','PEN',now()-interval '1 day',now()+interval '7 days',280,7,1,2),
                                                                                                                                                                     (3,'ACEPTADA','Incluye entrega','PEN',now()-interval '2 days',now()+interval '5 days',250,4,2,2);
INSERT INTO encargo(idencargo,idsolicitud,idcotizacion,estado) OVERRIDING SYSTEM VALUE VALUES(1,2,3,'CONFIRMADO');
INSERT INTO favorito(idfavorito,idcomprador,idproducto) OVERRIDING SYSTEM VALUE VALUES(1,1,2),(2,2,1);
INSERT INTO carrito(idcarrito,idcomprador,estado) OVERRIDING SYSTEM VALUE VALUES(1,1,'COMPRADO'),(2,1,'ACTIVO'),(3,2,'ACTIVO');
INSERT INTO carritodetalle(idcarritodetalle,cantidad,preciounitario,idcarrito,idproducto) OVERRIDING SYSTEM VALUE VALUES(1,2,100,1,1),(2,1,150,2,2),(3,1,100,3,1);
INSERT INTO boletaventa(idboletaventa,metodopago,estadoemision,preciototal,idcomprador,idcarrito,claveidempotencia,huellaoperacion) OVERRIDING SYSTEM VALUE VALUES
    (1,'SIMULADO','PENDIENTE',200,1,1,'demo-compra-1','e559344c9a2ea2eca8bc8f8870d73c9542656e036dbf4006868734afb074fd41');
INSERT INTO boletadetalle(idboletadetalle,cantidad,preciounitario,descuento,idboletaventa,idproducto) OVERRIDING SYSTEM VALUE VALUES(1,2,100,0,1,1);
SELECT setval(pg_get_serial_sequence('rol','idrol'),(SELECT max(idrol) FROM rol),true);
SELECT setval(pg_get_serial_sequence('usuario','usuarioid'),(SELECT max(usuarioid) FROM usuario),true);
SELECT setval(pg_get_serial_sequence('comprador','idcomprador'),(SELECT max(idcomprador) FROM comprador),true);
SELECT setval(pg_get_serial_sequence('vendedor','idvendedor'),(SELECT max(idvendedor) FROM vendedor),true);
SELECT setval(pg_get_serial_sequence('categoriaproducto','idcategoria'),(SELECT max(idcategoria) FROM categoriaproducto),true);
SELECT setval(pg_get_serial_sequence('categoriaprototipo','idcategoriaprototipo'),(SELECT max(idcategoriaprototipo) FROM categoriaprototipo),true);
SELECT setval(pg_get_serial_sequence('producto','idproducto'),(SELECT max(idproducto) FROM producto),true);
SELECT setval(pg_get_serial_sequence('imagenproducto','idimagen'),(SELECT max(idimagen) FROM imagenproducto),true);
SELECT setval(pg_get_serial_sequence('prototipoia','idprototipoia'),(SELECT max(idprototipoia) FROM prototipoia),true);
SELECT setval(pg_get_serial_sequence('imagenprototipo','idimagen'),(SELECT max(idimagen) FROM imagenprototipo),true);
SELECT setval(pg_get_serial_sequence('solicitud','idsolicitud'),(SELECT max(idsolicitud) FROM solicitud),true);
SELECT setval(pg_get_serial_sequence('cotizacionprototipo','idcotizacion'),(SELECT max(idcotizacion) FROM cotizacionprototipo),true);
SELECT setval(pg_get_serial_sequence('encargo','idencargo'),(SELECT max(idencargo) FROM encargo),true);
SELECT setval(pg_get_serial_sequence('favorito','idfavorito'),(SELECT max(idfavorito) FROM favorito),true);
SELECT setval(pg_get_serial_sequence('carrito','idcarrito'),(SELECT max(idcarrito) FROM carrito),true);
SELECT setval(pg_get_serial_sequence('carritodetalle','idcarritodetalle'),(SELECT max(idcarritodetalle) FROM carritodetalle),true);
SELECT setval(pg_get_serial_sequence('boletaventa','idboletaventa'),(SELECT max(idboletaventa) FROM boletaventa),true);
SELECT setval(pg_get_serial_sequence('boletadetalle','idboletadetalle'),(SELECT max(idboletadetalle) FROM boletadetalle),true);
