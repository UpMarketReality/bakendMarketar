-- 1. Insertar Roles
INSERT INTO Rol (Nombre, Descripcion) VALUES
                                          ('VENDEDOR', 'Acceso para gestionar tienda y productos'),
                                          ('COMPRADOR', 'Acceso para buscar y comprar productos');

-- 2. Insertar Usuarios (Asumiendo IdRol = 1 para Vendedor y IdRol = 2 para Comprador)
INSERT INTO Usuario (Nombre, PasswordHash, Correo, FechaNacimiento, Pais, Telefono, IdRol) VALUES
                                                                                               ('Admin InnovaTech', '$2a$12$566MsdpEZ3/8PLCIxPwyqOP/8XJ8naGFghROoHiCHSQ4SuEvgEnmO', 'admin@innovatech.com', '1995-08-15', 'Perú', '999888777', 1),
                                                                                               ('Carlos Comprador', '$2a$12$iyWzL4VRMaQCMcMeZkhPHOjiJFQoW9gt2XnMkoOOLgJfsDqjVh3U2', 'carlos@gmail.com', '1998-11-20', 'Perú', '999111222', 2);

-- 3. Insertar Vendedor (Enlazado al Usuario 1)
INSERT INTO Vendedor (NombreTienda, DescripcionTienda, RUC, UsuarioId) VALUES
    ('InnovaTech', 'Tienda especializada en tecnología y hardware', '20123456789', 1);

-- 4. Insertar Comprador (Enlazado al Usuario 2)
INSERT INTO Comprador (Direccion, UsuarioId) VALUES
    ('Av. Javier Prado Este 123, Lima', 2);

-- 5. Insertar Categorías de Productos
INSERT INTO CategoriaProducto (Nombre, Descripcion, Tipo) VALUES
                                                              ('Periféricos', 'Teclados, mouses y audífonos', 'TECNOLOGIA'),
                                                              ('Componentes', 'Placas, procesadores y almacenamiento', 'HARDWARE');

-- 6. Insertar Productos
-- (Enlazados al Vendedor 1 y a las Categorías 1 y 2)
INSERT INTO Producto (NombreProducto, Descripcion, PrecioUnidad, PrecioXMayor, Dimensiones, Material, Color, Stock, Estado, IdVendedor, IdCategoria) VALUES
-- Producto 1: Activo y con stock (Aparecerá para el comprador)
('Teclado Mecánico RGB', 'Switches azules, formato TKL', 150.00, 135.00, '35x15x4 cm', 'Plástico y Aluminio', 'Negro', 50, 'ACTIVO', 1, 1),

-- Producto 2: Activo y con stock (Aparecerá para el comprador)
('SSD M.2 1TB PCIe 4.0', 'Unidad de estado sólido de alto rendimiento', 320.00, 300.00, '80x22 mm', 'Silicio', 'Negro', 15, 'ACTIVO', 1, 2),

-- Producto 3: Agotado (No aparecerá para el comprador debido a Stock = 0)
('Monitor FHD 144Hz 24"', 'Panel IPS para gaming', 750.00, 720.00, '54x41x20 cm', 'Plástico', 'Negro', 0, 'AGOTADO', 1, 1),

-- Producto 4: Inactivo / Borrado lógico (No aparecerá para el comprador debido a Estado = INACTIVO)
('Mouse Óptico Básico', 'Mouse alámbrico 1000 DPI', 25.00, 20.00, '10x6x3 cm', 'Plástico', 'Blanco', 100, 'INACTIVO', 1, 1);


-- 1. Insertar Categorías de Prototipos
INSERT INTO CategoriaPrototipo (Nombre, Descripcion, Tipo) VALUES
                                                               ('Muebles Personalizados', 'Diseño de mobiliario a medida en madera, metal o mixtos', 'MOBILIARIO'),
                                                               ('Cerámicas Decorativas', 'Objetos funcionales y decorativos en arcilla, cerámica o porcelana', 'DECORACION'),
                                                               ('Accesorios de Oficina', 'Organizadores y elementos de escritorio impresos en 3D o madera', 'OFICINA');

-- 2. Insertar Prototipos IA (Asumiendo IdComprador = 1)
INSERT INTO PrototipoIA (NombrePrototipo, Prompt, Especificaciones, ModeloIA, EstadoGeneracion, IdComprador, IdCategoriaPrototipo) VALUES

-- Prototipo 1: Mueble (Completado)
('Silla Minimalista de Roble',
 'Generar un modelo 3D de una silla minimalista hecha de madera de roble con cojines de tela gris oscuro, diseño ergonómico escandinavo.',
 'Altura total: 90cm, Ancho: 45cm. Materiales: Madera de roble claro y lino.',
 'Midjourney v6',
 'COMPLETADO',
 1,
 1),

-- Prototipo 2: Cerámica (Generando)
('Jarrón Geométrico Texturizado',
 'Diseño de un jarrón de cerámica con patrones geométricos en relieve, estilo moderno y orgánico, color terracota mate bajo iluminación de estudio.',
 'Diámetro base: 15cm, Altura: 35cm. Acabado superficial: Mate rugoso. Sin asas.',
 'DALL-E 3',
 'GENERANDO',
 1,
 2),

-- Prototipo 3: Accesorio de Escritorio (Pendiente)
('Organizador Modular Hexagonal',
 'Render 3D de un organizador de escritorio compuesto por 5 módulos hexagonales magnéticos, hechos de plástico reciclado negro mate.',
 'Dimensiones por módulo: 10x10x12cm. Enganches magnéticos de neodimio en los laterales.',
 'Stable Diffusion XL',
 'PENDIENTE',
 1,
 3),

-- Prototipo 4: Mueble complejo (Error)
('Mesa de Centro Transformable',
 'Mesa de centro estilo industrial en acero y nogal que se eleva para convertirse en mesa de comedor, mecanismos expuestos.',
 'Largo: 120cm, Ancho: 70cm, Altura variable: 45cm a 75cm. Sistema de elevación hidráulico oculto en las patas.',
 'Midjourney v6',
 'ERROR',
 1,
 1);
