-- phpMyAdmin SQL Dump
-- version 4.6.6deb5
-- https://www.phpmyadmin.net/
--
-- Host: localhost:3306
-- Generation Time: Aug 13, 2019 at 01:30 PM
-- Server version: 5.7.27-0ubuntu0.18.04.1
-- PHP Version: 7.2.19-0ubuntu0.18.04.1

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `sigProdBd`
--

-- --------------------------------------------------------

--
-- Table structure for table `CaracteristicasCurvaRele`
--

CREATE TABLE `CaracteristicasCurvaRele` (
  `codigoRele` int(11) DEFAULT NULL,
  `isFase` tinyint(1) DEFAULT NULL,
  `nome` varchar(50) DEFAULT NULL,
  `a` double DEFAULT NULL,
  `b` double DEFAULT NULL,
  `p` double DEFAULT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `CaracteristicasCurvaReligador`
--

CREATE TABLE `CaracteristicasCurvaReligador` (
  `codigoReligador` int(11) DEFAULT NULL,
  `isFase` tinyint(1) DEFAULT NULL,
  `isRapida` tinyint(1) DEFAULT NULL,
  `nome` varchar(50) DEFAULT NULL,
  `a` double DEFAULT NULL,
  `b` double DEFAULT NULL,
  `p` double DEFAULT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `CorrentePickupDefinidoEletromecanicoRele`
--

CREATE TABLE `CorrentePickupDefinidoEletromecanicoRele` (
  `codigoRele` int(11) NOT NULL,
  `correntePickup` double NOT NULL,
  `isFase` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `CorrentePickupDefinidoEletromecanicoReligador`
--

CREATE TABLE `CorrentePickupDefinidoEletromecanicoReligador` (
  `codigoReligador` int(11) NOT NULL,
  `correntePickup` double NOT NULL,
  `isFase` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `CorrenteTempoDigitalRele`
--

CREATE TABLE `CorrenteTempoDigitalRele` (
  `codigoRele` int(11) NOT NULL,
  `tipo` int(11) NOT NULL,
  `correnteMinino` double DEFAULT NULL,
  `correnteMaximo` double DEFAULT NULL,
  `correntePasso` double DEFAULT NULL,
  `tempoMaximo` double DEFAULT NULL,
  `tempoMinimo` double DEFAULT NULL,
  `tempoPasso` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `CorrenteTempoDigitalReligador`
--

CREATE TABLE `CorrenteTempoDigitalReligador` (
  `codigoReligador` int(11) NOT NULL,
  `tipo` int(11) NOT NULL,
  `correnteMinino` double DEFAULT NULL,
  `correnteMaximo` double DEFAULT NULL,
  `correntePasso` double DEFAULT NULL,
  `tempoMaximoRapido` double DEFAULT NULL,
  `tempoMinimoRapido` double DEFAULT NULL,
  `tempoPassoRapido` double DEFAULT NULL,
  `tempoMaximoLento` double DEFAULT NULL,
  `tempoMinimoLento` double DEFAULT NULL,
  `tempoPassoLento` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Elo`
--

CREATE TABLE `Elo` (
  `correnteNominal` int(11) NOT NULL,
  `preferencial` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `Elo`
--

INSERT INTO `Elo` (`correnteNominal`, `preferencial`) VALUES
(6, 0),
(10, 0),
(15, 0),
(25, 0),
(40, 0),
(65, 0);

-- --------------------------------------------------------

--
-- Table structure for table `ElotransformadorMono`
--

CREATE TABLE `ElotransformadorMono` (
  `potenciaId` int(11) DEFAULT NULL,
  `kvId` int(11) DEFAULT NULL,
  `tipo` varchar(1) DEFAULT NULL,
  `corrente` varchar(5) DEFAULT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `ElotransformadorTri`
--

CREATE TABLE `ElotransformadorTri` (
  `potenciaId` int(11) DEFAULT NULL,
  `kvId` int(11) DEFAULT NULL,
  `tipo` varchar(1) DEFAULT NULL,
  `corrente` int(11) DEFAULT NULL,
  `id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `KvMono`
--

CREATE TABLE `KvMono` (
  `id` int(11) NOT NULL,
  `kv` varchar(5) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `KvTri`
--

CREATE TABLE `KvTri` (
  `id` int(11) NOT NULL,
  `kv` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `PontoCurvaElo`
--

CREATE TABLE `PontoCurvaElo` (
  `id` int(11) NOT NULL,
  `corrente` double DEFAULT NULL,
  `tempo` double DEFAULT NULL,
  `correnteElo` int(11) DEFAULT NULL,
  `ehCurvaDeMaxima` tinyint(1) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Dumping data for table `PontoCurvaElo`
--

INSERT INTO `PontoCurvaElo` (`id`, `corrente`, `tempo`, `correnteElo`, `ehCurvaDeMaxima`) VALUES
(1, 12.232, 300, 6, 0),
(2, 12.492, 80.239, 6, 0),
(3, 12.592, 60, 6, 0),
(4, 12.636, 54.131, 6, 0),
(5, 12.736, 46.674, 6, 0),
(6, 12.93, 40, 6, 0),
(7, 13.079, 35, 6, 0),
(8, 13.23, 30, 6, 0),
(9, 13.437, 26.148, 6, 0),
(10, 13.537, 22.79, 6, 0),
(11, 13.694, 20, 6, 0),
(12, 14.502, 12.951, 6, 0),
(13, 14.702, 11.552, 6, 0),
(14, 15, 10, 6, 0),
(15, 15.359, 9, 6, 0),
(16, 15.559, 8.307, 6, 0),
(17, 15.716, 7.385, 6, 0),
(18, 15.916, 7, 6, 0),
(19, 16.081, 6.535, 6, 0),
(20, 16.454, 6, 6, 0),
(21, 16.644, 5.6, 6, 0),
(22, 16.836, 5, 6, 0),
(23, 17.031, 4.669, 6, 0),
(24, 17.627, 4, 6, 0),
(25, 17.831, 3.608, 6, 0),
(26, 18.455, 3, 6, 0),
(27, 19.323, 2.5, 6, 0),
(28, 20, 2.128, 6, 0),
(29, 22.229, 1.5, 6, 0),
(30, 23.348, 1.259, 6, 0),
(31, 25, 1, 6, 0),
(32, 26.123, 0.9, 6, 0),
(33, 27.156, 0.8, 6, 0),
(34, 28.78, 0.7, 6, 0),
(35, 30.5, 0.6, 6, 0),
(36, 32.881, 0.5, 6, 0),
(37, 36, 0.4, 6, 0),
(38, 41.117, 0.3, 6, 0),
(39, 50, 0.2, 6, 0),
(40, 70, 0.1, 6, 0),
(41, 73.827, 0.09, 6, 0),
(42, 77.293, 0.08, 6, 0),
(43, 83.068, 0.07, 6, 0),
(44, 90, 0.06, 6, 0),
(45, 100, 0.05, 6, 0),
(46, 111.434, 0.04, 6, 0),
(47, 129.417, 0.03, 6, 0),
(48, 162.696, 0.02, 6, 0),
(49, 228.303, 0.01, 6, 0),
(50, 14.839, 300, 6, 1),
(51, 15.359, 66.866, 6, 1),
(52, 15.716, 44.088, 6, 1),
(53, 15.897, 34.847, 6, 1),
(54, 16.266, 23.338, 6, 1),
(55, 16.836, 16.858, 6, 1),
(56, 17.426, 13.122, 6, 1),
(57, 18.037, 10.331, 6, 1),
(58, 18.669, 8.499, 6, 1),
(59, 19.103, 6.921, 6, 1),
(60, 19.772, 5.796, 6, 1),
(61, 20.121, 4.776, 6, 1),
(62, 20.351, 4.359, 6, 1),
(63, 21.061, 3.779, 6, 1),
(64, 21.301, 3.412, 6, 1),
(65, 21.794, 3.151, 6, 1),
(66, 22.044, 2.933, 6, 1),
(67, 22.812, 2.561, 6, 1),
(68, 23.073, 2.419, 6, 1),
(69, 23.877, 2.136, 6, 1),
(70, 24.151, 1.977, 6, 1),
(71, 24.992, 1.765, 6, 1),
(72, 25.863, 1.557, 6, 1),
(73, 26.764, 1.422, 6, 1),
(74, 27.071, 1.359, 6, 1),
(75, 27.696, 1.269, 6, 1),
(76, 28.014, 1.213, 6, 1),
(77, 28.991, 1.107, 6, 1),
(78, 30.734, 0.966, 6, 1),
(79, 31.809, 0.879, 6, 1),
(80, 32.175, 0.841, 6, 1),
(81, 33.684, 0.731, 6, 1),
(82, 36.081, 0.638, 6, 1),
(83, 38.208, 0.559, 6, 1),
(84, 41.164, 0.466, 6, 1),
(85, 45.627, 0.377, 6, 1),
(86, 48.313, 0.333, 6, 1),
(87, 53.498, 0.276, 6, 1),
(88, 59.315, 0.231, 6, 1),
(89, 64.607, 0.195, 6, 1),
(90, 72.172, 0.161, 6, 1),
(91, 84.992, 0.122, 6, 1),
(92, 95.532, 0.0977, 6, 1),
(93, 110.164, 0.0772, 6, 1),
(94, 159.007, 0.0441, 6, 1),
(95, 212.763, 0.0281, 6, 1),
(96, 261.475, 0.0227, 6, 1),
(97, 321.758, 0.0188, 6, 1),
(98, 364.971, 0.0174, 6, 1),
(99, 411.596, 0.0158, 6, 1),
(100, 461.505, 0.0148, 6, 1),
(101, 523.013, 0.0136, 6, 1),
(102, 600, 0.013, 6, 1),
(103, 20.7, 300, 10, 0),
(104, 20.984, 250, 10, 0),
(105, 21.473, 126.542, 10, 0),
(106, 21.723, 107.81, 10, 0),
(107, 21.974, 83.051, 10, 0),
(108, 22.229, 63.127, 10, 0),
(109, 22.529, 53.515, 10, 0),
(110, 22.748, 50, 10, 0),
(111, 23.012, 40, 10, 0),
(112, 23.212, 35, 10, 0),
(113, 23.548, 32.175, 10, 0),
(114, 23.821, 26.449, 10, 0),
(115, 24.098, 22.531, 10, 0),
(116, 24.66, 18.461, 10, 0),
(117, 25, 15, 10, 0),
(118, 26, 11.955, 10, 0),
(119, 26.733, 10, 10, 0),
(120, 27.043, 8.402, 10, 0),
(121, 27.356, 7.385, 10, 0),
(122, 27.994, 6.535, 10, 0),
(123, 28.647, 6, 10, 0),
(124, 28.98, 5.6, 10, 0),
(125, 29.656, 4.5, 10, 0),
(126, 30, 4.213, 10, 0),
(127, 31.399, 3.5, 10, 0),
(128, 31.763, 3.217, 10, 0),
(129, 32.881, 2.8, 10, 0),
(130, 33.648, 2.5, 10, 0),
(131, 35, 2.056, 10, 0),
(132, 37.159, 1.701, 10, 0),
(133, 40, 1.304, 10, 0),
(134, 43.551, 1, 10, 0),
(135, 45, 0.9, 10, 0),
(136, 47.25, 0.8, 10, 0),
(137, 50, 0.7, 10, 0),
(138, 53.481, 0.6, 10, 0),
(139, 57.965, 0.5, 10, 0),
(140, 63.127, 0.4, 10, 0),
(141, 73.811, 0.3, 10, 0),
(142, 90, 0.2, 10, 0),
(143, 127.751, 0.1, 10, 0),
(144, 135.307, 0.09, 10, 0),
(145, 144.967, 0.08, 10, 0),
(146, 155, 0.07, 10, 0),
(147, 169.274, 0.06, 10, 0),
(148, 184.539, 0.05, 10, 0),
(149, 203, 0.04, 10, 0),
(150, 235.751, 0.03, 10, 0),
(151, 283.269, 0.02, 10, 0),
(152, 400, 0.01, 10, 0),
(153, 24.151, 300, 10, 1),
(154, 24.709, 111.716, 10, 1),
(155, 25.863, 63.144, 10, 1),
(156, 27.071, 34.847, 10, 1),
(157, 28.336, 22.553, 10, 1),
(158, 30.734, 13.578, 10, 1),
(159, 32.546, 8.897, 10, 1),
(160, 35.264, 5.665, 10, 1),
(161, 38.208, 3.779, 10, 1),
(162, 41.164, 2.648, 10, 1),
(163, 45.627, 1.847, 10, 1),
(164, 52.285, 1.227, 10, 1),
(165, 57.309, 0.945, 10, 1),
(166, 64.607, 0.684, 10, 1),
(167, 74.692, 0.488, 10, 1),
(168, 84.992, 0.369, 10, 1),
(169, 96.629, 0.277, 10, 1),
(170, 108.907, 0.233, 10, 1),
(171, 120.749, 0.191, 10, 1),
(172, 138.564, 0.148, 10, 1),
(173, 157.194, 0.119, 10, 1),
(174, 182.467, 0.0889, 10, 1),
(175, 207.945, 0.0713, 10, 1),
(176, 215.214, 0.0638, 10, 1),
(177, 230.523, 0.0579, 10, 1),
(178, 244.111, 0.0523, 10, 1),
(179, 261.476, 0.0472, 10, 1),
(180, 283.302, 0.042, 10, 1),
(181, 307.347, 0.0365, 10, 1),
(182, 340.726, 0.0322, 10, 1),
(183, 373.429, 0.0286, 10, 1),
(184, 406.912, 0.0261, 10, 1),
(185, 456.253, 0.0233, 10, 1),
(186, 505.363, 0.0211, 10, 1),
(187, 560.185, 0.0195, 10, 1),
(188, 617.277, 0.0182, 10, 1),
(189, 676.397, 0.0168, 10, 1),
(190, 755.501, 0.0155, 10, 1),
(191, 800, 0.015, 10, 1),
(192, 849.913, 0.0143, 10, 1),
(193, 977.386, 0.0131, 10, 1),
(194, 1000, 0.013, 10, 1),
(195, 31, 300, 15, 0),
(196, 31.399, 235.751, 15, 0),
(197, 31.763, 174.338, 15, 0),
(198, 32.131, 129.472, 15, 0),
(199, 32.881, 107.81, 15, 0),
(200, 33.262, 92.294, 15, 0),
(201, 34.038, 74.679, 15, 0),
(202, 34.433, 64.593, 15, 0),
(203, 34.832, 54.131, 15, 0),
(204, 35, 50, 15, 0),
(205, 36.058, 42.095, 15, 0),
(206, 36.476, 36.917, 15, 0),
(207, 37.326, 32.546, 15, 0),
(208, 37.759, 28.33, 15, 0),
(209, 38.54, 25, 15, 0),
(210, 39.341, 22.021, 15, 0),
(211, 40, 17.636, 15, 0),
(212, 41.117, 15, 15, 0),
(213, 42.561, 11.955, 15, 0),
(214, 44.055, 10, 15, 0),
(215, 45, 8.307, 15, 0),
(216, 47.204, 7, 15, 0),
(217, 48.303, 6.101, 15, 0),
(218, 49.427, 5.41, 15, 0),
(219, 50, 5, 15, 0),
(220, 50.492, 4.616, 15, 0),
(221, 51.667, 4.261, 15, 0),
(222, 53.481, 3.777, 15, 0),
(223, 54.725, 3.5, 15, 0),
(224, 55.999, 3.144, 15, 0),
(225, 57.302, 2.675, 15, 0),
(226, 60, 2.253, 15, 0),
(227, 65.156, 1.761, 15, 0),
(228, 67.629, 1.5, 15, 0),
(229, 73.811, 1.162, 15, 0),
(230, 78.18, 1, 15, 0),
(231, 81, 0.9, 15, 0),
(232, 83.603, 0.8, 15, 0),
(233, 87.957, 0.7, 15, 0),
(234, 92, 0.6, 15, 0),
(235, 100, 0.5, 15, 0),
(236, 110.021, 0.4, 15, 0),
(237, 126.291, 0.3, 15, 0),
(238, 155.317, 0.2, 15, 0),
(239, 222.604, 0.1, 15, 0),
(240, 233.061, 0.09, 15, 0),
(241, 249.674, 0.08, 15, 0),
(242, 267.472, 0.07, 15, 0),
(243, 289.847, 0.06, 15, 0),
(244, 317.953, 0.05, 15, 0),
(245, 356.625, 0.04, 15, 0),
(246, 415.911, 0.03, 15, 0),
(247, 505.051, 0.02, 15, 0),
(248, 698.014, 0.01, 15, 0),
(249, 39.094, 300, 15, 1),
(250, 39.544, 264.421, 15, 1),
(251, 40.696, 125.229, 15, 1),
(252, 41.164, 98.864, 15, 1),
(253, 42.116, 84.024, 15, 1),
(254, 42.601, 71.351, 15, 1),
(255, 43.091, 59.317, 15, 1),
(256, 44.088, 51.123, 15, 1),
(257, 45.627, 39.091, 15, 1),
(258, 46.683, 28.991, 15, 1),
(259, 48.313, 22.297, 15, 1),
(260, 51.099, 15.044, 15, 1),
(261, 52.888, 11.979, 15, 1),
(262, 53.498, 10.331, 15, 1),
(263, 55.371, 8.403, 15, 1),
(264, 57.309, 6.763, 15, 1),
(265, 59.315, 5.665, 15, 1),
(266, 61.715, 4.359, 15, 1),
(267, 63.872, 3.779, 15, 1),
(268, 65.351, 3.297, 15, 1),
(269, 67.636, 2.899, 15, 1),
(270, 70.539, 2.419, 15, 1),
(271, 73.003, 2.161, 15, 1),
(272, 76.421, 1.889, 15, 1),
(273, 82.122, 1.541, 15, 1),
(274, 85.971, 1.328, 15, 1),
(275, 91.264, 1.121, 15, 1),
(276, 95.532, 0.966, 15, 1),
(277, 105.22, 0.773, 15, 1),
(278, 111.434, 0.676, 15, 1),
(279, 116.665, 0.593, 15, 1),
(280, 129.351, 0.477, 15, 1),
(281, 141.779, 0.386, 15, 1),
(282, 155.402, 0.314, 15, 1),
(283, 172.298, 0.262, 15, 1),
(284, 195.464, 0.197, 15, 1),
(285, 230.524, 0.143, 15, 1),
(286, 270.616, 0.111, 15, 1),
(287, 310.888, 0.0849, 15, 1),
(288, 369.176, 0.0646, 15, 1),
(289, 421.126, 0.0534, 15, 1),
(290, 466.817, 0.0466, 15, 1),
(291, 529.033, 0.0382, 15, 1),
(292, 617.277, 0.0307, 15, 1),
(293, 729.997, 0.0252, 15, 1),
(294, 849.914, 0.0213, 15, 1),
(295, 966.272, 0.0182, 15, 1),
(296, 1165.524, 0.0158, 15, 1),
(297, 1371.575, 0.0139, 15, 1),
(298, 1704.255, 0.0131, 15, 1),
(299, 2000, 0.013, 15, 1),
(300, 50.492, 300, 25, 0),
(301, 51.667, 168.454, 25, 0),
(302, 52.265, 132.469, 25, 0),
(303, 53.481, 100, 25, 0),
(304, 54.1, 84.979, 25, 0),
(305, 55.358, 70, 25, 0),
(306, 55.999, 56.02, 25, 0),
(307, 56.647, 50, 25, 0),
(308, 57.302, 40, 25, 0),
(309, 57.931, 35, 25, 0),
(310, 59.02, 30, 25, 0),
(311, 60, 25.555, 25, 0),
(312, 60.989, 22.021, 25, 0),
(313, 61.694, 20, 25, 0),
(314, 62.406, 18.673, 25, 0),
(315, 63.127, 16.279, 25, 0),
(316, 64.593, 15, 25, 0),
(317, 65.339, 12.951, 25, 0),
(318, 66.857, 11.685, 25, 0),
(319, 67.629, 10, 25, 0),
(320, 69.2, 9, 25, 0),
(321, 70, 8, 25, 0),
(322, 72.967, 7, 25, 0),
(323, 74.665, 6.101, 25, 0),
(324, 75.529, 5.665, 25, 0),
(325, 78.18, 5, 25, 0),
(326, 79.085, 4.5, 25, 0),
(327, 82.103, 3.608, 25, 0),
(328, 84.979, 3.18, 25, 0),
(329, 87.957, 2.833, 25, 0),
(330, 94.419, 2.253, 25, 0),
(331, 100, 1.802, 25, 0),
(332, 110.021, 1.319, 25, 0),
(333, 122.148, 1, 25, 0),
(334, 127.751, 0.9, 25, 0),
(335, 133.761, 0.8, 25, 0),
(336, 141.673, 0.7, 25, 0),
(337, 152.542, 0.6, 25, 0),
(338, 164.624, 0.5, 25, 0),
(339, 184.539, 0.4, 25, 0),
(340, 210.19, 0.3, 25, 0),
(341, 258.42, 0.2, 25, 0),
(342, 373.38, 0.1, 25, 0),
(343, 390.922, 0.09, 25, 0),
(344, 420.725, 0.08, 25, 0),
(345, 456.021, 0.07, 25, 0),
(346, 487.622, 0.06, 25, 0),
(347, 524.755, 0.05, 25, 0),
(348, 593.148, 0.04, 25, 0),
(349, 668.601, 0.03, 25, 0),
(350, 821.084, 0.02, 25, 0),
(351, 1165.914, 0.01, 25, 0),
(352, 60.317, 300, 25, 1),
(353, 61.715, 225.174, 25, 1),
(354, 62.426, 160.993, 25, 1),
(355, 63.872, 125.229, 25, 1),
(356, 64.607, 97.735, 25, 1),
(357, 65.352, 78.191, 25, 1),
(358, 66.866, 62.426, 25, 1),
(359, 70.539, 27.696, 25, 1),
(360, 73.003, 19.773, 25, 1),
(361, 74.692, 16.858, 25, 1),
(362, 76.421, 12.681, 25, 1),
(363, 79.089, 9.886, 25, 1),
(364, 82.122, 8.499, 25, 1),
(365, 84.992, 7.061, 25, 1),
(366, 86.961, 5.931, 25, 1),
(367, 91.263, 4.943, 25, 1),
(368, 95.532, 3.911, 25, 1),
(369, 106.438, 2.966, 25, 1),
(370, 112.719, 2.338, 25, 1),
(371, 124.976, 1.631, 25, 1),
(372, 159.007, 0.849, 25, 1),
(373, 174.286, 0.684, 25, 1),
(374, 195.464, 0.553, 25, 1),
(375, 210.341, 0.456, 25, 1),
(376, 230.524, 0.377, 25, 1),
(377, 264.487, 0.276, 25, 1),
(378, 314.471, 0.205, 25, 1),
(379, 373.429, 0.143, 25, 1),
(380, 421.126, 0.115, 25, 1),
(381, 461.505, 0.0977, 25, 1),
(382, 511.179, 0.0821, 25, 1),
(383, 566.633, 0.0676, 25, 1),
(384, 646.162, 0.0553, 25, 1),
(385, 729.997, 0.0477, 25, 1),
(386, 849.913, 0.0382, 25, 1),
(387, 955.283, 0.0325, 25, 1),
(388, 1116.525, 0.0273, 25, 1),
(389, 1280.665, 0.0233, 25, 1),
(390, 1500, 0.02, 25, 1),
(391, 1555.331, 0.0191, 25, 1),
(392, 1763.707, 0.0166, 25, 1),
(393, 2127.593, 0.0141, 25, 1),
(394, 2675.328, 0.0131, 25, 1),
(395, 3000, 0.013, 25, 1),
(396, 80, 300, 40, 0),
(397, 81.166, 250, 40, 0),
(398, 83.051, 200, 40, 0),
(399, 83.251, 178.374, 40, 0),
(400, 84.009, 150, 40, 0),
(401, 84.979, 123.679, 40, 0),
(402, 85.96, 100, 40, 0),
(403, 87.957, 90, 40, 0),
(404, 88.972, 80, 40, 0),
(405, 90, 70, 40, 0),
(406, 92.275, 60, 40, 0),
(407, 93.341, 50, 40, 0),
(408, 95.51, 44.58, 40, 0),
(409, 96.613, 40, 40, 0),
(410, 97.729, 35, 40, 0),
(411, 100, 30, 40, 0),
(412, 103.877, 25, 40, 0),
(413, 106.292, 20, 40, 0),
(414, 108.764, 17.04, 40, 0),
(415, 113.881, 12.515, 40, 0),
(416, 119.238, 10, 40, 0),
(417, 120.617, 9, 40, 0),
(418, 123.421, 8, 40, 0),
(419, 124.848, 7, 40, 0),
(420, 129.228, 6, 40, 0),
(421, 135.307, 5, 40, 0),
(422, 141.673, 4, 40, 0),
(423, 146.643, 3.5, 40, 0),
(424, 150, 3, 40, 0),
(425, 158.928, 2.5, 40, 0),
(426, 168.329, 2, 40, 0),
(427, 178.285, 1.643, 40, 0),
(428, 193.221, 1.274, 40, 0),
(429, 210.19, 1, 40, 0),
(430, 217.07, 0.9, 40, 0),
(431, 224.604, 0.8, 40, 0),
(432, 238.472, 0.7, 40, 0),
(433, 250, 0.6, 40, 0),
(434, 270.56, 0.5, 40, 0),
(435, 283.269, 0.45, 40, 0),
(436, 296.576, 0.4, 40, 0),
(437, 314.325, 0.35, 40, 0),
(438, 332.892, 0.3, 40, 0),
(439, 364.906, 0.25, 40, 0),
(440, 400, 0.2, 40, 0),
(441, 472.04, 0.15, 40, 0),
(442, 573.06, 0.1, 40, 0),
(443, 593.148, 0.09, 40, 0),
(444, 638.611, 0.08, 40, 0),
(445, 676.316, 0.07, 40, 0),
(446, 729.883, 0.06, 40, 0),
(447, 800, 0.05, 40, 0),
(448, 900, 0.04, 40, 0),
(449, 1051.45, 0.03, 40, 0),
(450, 1292.84, 0.02, 40, 0),
(451, 1824.477, 0.01, 40, 0),
(452, 102.838, 300, 40, 1),
(453, 104.025, 132.587, 40, 1),
(454, 105.224, 97.735, 40, 1),
(455, 107.666, 59.317, 40, 1),
(456, 110.164, 49.431, 40, 1),
(457, 114.021, 34.847, 40, 1),
(458, 116.665, 25.279, 40, 1),
(459, 122.142, 19.109, 40, 1),
(460, 124.976, 14.374, 40, 1),
(461, 132.351, 9.664, 40, 1),
(462, 140.162, 7.473, 40, 1),
(463, 148.434, 5.796, 40, 1),
(464, 160.842, 4.261, 40, 1),
(465, 172.298, 3.335, 40, 1),
(466, 182.467, 2.741, 40, 1),
(467, 205.577, 1.911, 40, 1),
(468, 230.524, 1.375, 40, 1),
(469, 258.498, 1.023, 40, 1),
(470, 283.302, 0.764, 40, 1),
(471, 310.888, 0.609, 40, 1),
(472, 344.653, 0.472, 40, 1),
(473, 377.732, 0.373, 40, 1),
(474, 406.913, 0.318, 40, 1),
(475, 461.505, 0.238, 40, 1),
(476, 523.013, 0.182, 40, 1),
(477, 593.173, 0.147, 40, 1),
(478, 684.175, 0.111, 40, 1),
(479, 755.501, 0.0944, 40, 1),
(480, 849.914, 0.0773, 40, 1),
(481, 955.284, 0.0631, 40, 1),
(482, 1054.505, 0.0535, 40, 1),
(483, 1182.192, 0.0466, 40, 1),
(484, 1325.341, 0.0391, 40, 1),
(485, 1502.903, 0.0325, 40, 1),
(486, 1684.886, 0.0283, 40, 1),
(487, 1867.438, 0.0247, 40, 1),
(488, 2103.363, 0.0221, 40, 1),
(489, 2305.205, 0.0197, 40, 1),
(490, 2614.738, 0.0176, 40, 1),
(491, 2965.834, 0.0161, 40, 1),
(492, 3486.241, 0.0146, 40, 1),
(493, 4068.916, 0.0136, 40, 1),
(494, 4614.959, 0.0131, 40, 1),
(495, 5000, 0.013, 40, 1),
(496, 129.228, 300, 65, 0),
(497, 130.721, 250, 65, 0),
(498, 133.761, 168.454, 65, 0),
(499, 135.307, 126.542, 65, 0),
(500, 138.454, 100, 65, 0),
(501, 140.054, 90, 65, 0),
(502, 141.673, 70, 65, 0),
(503, 144.967, 60, 65, 0),
(504, 146.643, 50, 65, 0),
(505, 148.338, 45, 65, 0),
(506, 150, 40, 65, 0),
(507, 153.542, 35, 65, 0),
(508, 157.112, 30, 65, 0),
(509, 158.928, 25, 65, 0),
(510, 164.503, 20, 65, 0),
(511, 168.329, 16.566, 65, 0),
(512, 176.248, 12.372, 65, 0),
(513, 182.431, 10, 65, 0),
(514, 186.673, 9, 65, 0),
(515, 191.013, 8, 65, 0),
(516, 195.455, 7, 65, 0),
(517, 200, 6, 65, 0),
(518, 205.42, 5, 65, 0),
(519, 220.064, 4, 65, 0),
(520, 225.173, 3.5, 65, 0),
(521, 233.061, 3, 65, 0),
(522, 244.009, 2.5, 65, 0),
(523, 258.42, 2, 65, 0),
(524, 321.624, 1, 65, 0),
(525, 329.092, 0.9, 65, 0),
(526, 340.622, 0.8, 65, 0),
(527, 364.906, 0.7, 65, 0),
(528, 386.46, 0.6, 65, 0),
(529, 420.725, 0.5, 65, 0),
(530, 445.645, 0.45, 65, 0),
(531, 456.021, 0.4, 65, 0),
(532, 494.278, 0.35, 65, 0),
(533, 522.755, 0.3, 65, 0),
(534, 573.06, 0.25, 65, 0),
(535, 624.124, 0.2, 65, 0),
(536, 729.883, 0.15, 65, 0),
(537, 900, 0.1, 65, 0),
(538, 955.065, 0.09, 65, 0),
(539, 1015.849, 0.08, 65, 0),
(540, 1088.298, 0.07, 65, 0),
(541, 1192.998, 0.06, 65, 0),
(542, 1292.84, 0.05, 65, 0),
(543, 1450.138, 0.04, 65, 0),
(544, 1683.578, 0.03, 65, 0),
(545, 2031.18, 0.02, 65, 0),
(546, 2965.785, 0.01, 65, 0),
(547, 155.403, 300, 65, 1),
(548, 159.007, 264.421, 65, 1),
(549, 160.842, 215.075, 65, 1),
(550, 162.697, 168.517, 65, 1),
(551, 168.392, 118.279, 65, 1),
(552, 174.286, 84.992, 65, 1),
(553, 176.296, 71.351, 65, 1),
(554, 182.467, 51.711, 65, 1),
(555, 188.854, 39.543, 65, 1),
(556, 195.465, 28.991, 65, 1),
(557, 203.236, 19.773, 65, 1),
(558, 210.341, 15.391, 65, 1),
(559, 220.201, 10.689, 65, 1),
(560, 230.524, 7.909, 65, 1),
(561, 244.111, 5.601, 65, 1),
(562, 261.476, 4.071, 65, 1),
(563, 273.734, 3.261, 65, 1),
(564, 286.566, 2.678, 65, 1),
(565, 333.009, 1.667, 65, 1),
(566, 369.176, 1.172, 65, 1),
(567, 421.127, 0.831, 65, 1),
(568, 461.505, 0.653, 65, 1),
(569, 517.062, 0.494, 65, 1),
(570, 586.423, 0.382, 65, 1),
(571, 653.591, 0.296, 65, 1),
(572, 738.402, 0.233, 65, 1),
(573, 840.237, 0.188, 65, 1),
(574, 955.284, 0.143, 65, 1),
(575, 1129.359, 0.106, 65, 1),
(576, 1266.111, 0.0859, 65, 1),
(577, 1435.738, 0.0713, 65, 1),
(578, 1646.807, 0.0573, 65, 1),
(579, 1867.438, 0.0477, 65, 1),
(580, 2103.362, 0.0415, 65, 1),
(581, 2278.952, 0.0364, 65, 1),
(582, 2584.959, 0.0307, 65, 1),
(583, 2833.016, 0.0264, 65, 1),
(584, 3217.591, 0.0222, 65, 1),
(585, 3734.295, 0.0191, 65, 1),
(586, 4211.084, 0.0168, 65, 1),
(587, 5000, 0.015, 65, 1),
(588, 5290.144, 0.0143, 65, 1),
(589, 6000, 0.0139, 65, 1),
(590, 7000, 0.0135, 65, 1),
(591, 8000, 0.0132, 65, 1),
(592, 9000, 0.013, 65, 1);

-- --------------------------------------------------------

--
-- Table structure for table `PontoCurvaRele`
--

CREATE TABLE `PontoCurvaRele` (
  `id` int(11) NOT NULL,
  `corrente` double DEFAULT NULL,
  `tempo` double DEFAULT NULL,
  `correntePickup` double DEFAULT NULL,
  `isFase` tinyint(1) DEFAULT NULL,
  `dial` double DEFAULT NULL,
  `codigoRele` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `PontoCurvaReligador`
--

CREATE TABLE `PontoCurvaReligador` (
  `id` int(11) NOT NULL,
  `corrente` double DEFAULT NULL,
  `tempo` double DEFAULT NULL,
  `correntePickup` double DEFAULT NULL,
  `isFase` tinyint(1) DEFAULT NULL,
  `isRapida` tinyint(1) DEFAULT NULL,
  `dial` double DEFAULT NULL,
  `codigoReligador` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `PotenciaMono`
--

CREATE TABLE `PotenciaMono` (
  `id` int(11) NOT NULL,
  `potencia` varchar(6) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `PotenciaTri`
--

CREATE TABLE `PotenciaTri` (
  `id` int(11) NOT NULL,
  `potencia` varchar(10) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Rele`
--

CREATE TABLE `Rele` (
  `codigo` int(11) NOT NULL,
  `fabricante` varchar(50) DEFAULT NULL,
  `modelo` varchar(50) DEFAULT NULL,
  `isDigital` tinyint(1) DEFAULT NULL,
  `existeDefinidaFase` tinyint(1) NOT NULL,
  `existeDefinidaNeutro` tinyint(1) NOT NULL,
  `existeInversaFase` tinyint(1) NOT NULL,
  `existeInversaNeutro` tinyint(1) NOT NULL,
  `fatorInicioInvFase` double DEFAULT NULL,
  `fatorInicioInvNeutro` double DEFAULT NULL,
  `fatorInicioDefFase` double DEFAULT NULL,
  `fatorInicioDefNeutro` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `Religador`
--

CREATE TABLE `Religador` (
  `codigo` int(11) NOT NULL,
  `fabricante` varchar(50) DEFAULT NULL,
  `modelo` varchar(50) DEFAULT NULL,
  `isDigital` tinyint(1) DEFAULT NULL,
  `existeDefinidaFase` tinyint(1) NOT NULL,
  `existeDefinidaNeutro` tinyint(1) NOT NULL,
  `existeInversaFase` tinyint(1) NOT NULL,
  `existeInversaNeutro` tinyint(1) NOT NULL,
  `fatorInicioInvFase` double DEFAULT NULL,
  `fatorInicioInvNeutro` double DEFAULT NULL,
  `fatorInicioDefFase` double DEFAULT NULL,
  `fatorInicioDefNeutro` double DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `TempoAtuacaoDefinidoEletromecanicoRele`
--

CREATE TABLE `TempoAtuacaoDefinidoEletromecanicoRele` (
  `codigoRele` int(11) NOT NULL,
  `tempoAtuacao` double NOT NULL,
  `isFase` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `TempoAtuacaoDefinidoEletromecanicoReligador`
--

CREATE TABLE `TempoAtuacaoDefinidoEletromecanicoReligador` (
  `codigoReligador` int(11) NOT NULL,
  `tempoAtuacao` double NOT NULL,
  `isFase` tinyint(1) NOT NULL,
  `isRapida` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `CaracteristicasCurvaRele`
--
ALTER TABLE `CaracteristicasCurvaRele`
  ADD PRIMARY KEY (`id`),
  ADD KEY `codigoRele` (`codigoRele`);

--
-- Indexes for table `CaracteristicasCurvaReligador`
--
ALTER TABLE `CaracteristicasCurvaReligador`
  ADD PRIMARY KEY (`id`),
  ADD KEY `codigoReligador` (`codigoReligador`);

--
-- Indexes for table `CorrentePickupDefinidoEletromecanicoRele`
--
ALTER TABLE `CorrentePickupDefinidoEletromecanicoRele`
  ADD PRIMARY KEY (`codigoRele`,`isFase`,`correntePickup`);

--
-- Indexes for table `CorrentePickupDefinidoEletromecanicoReligador`
--
ALTER TABLE `CorrentePickupDefinidoEletromecanicoReligador`
  ADD PRIMARY KEY (`codigoReligador`,`isFase`,`correntePickup`);

--
-- Indexes for table `CorrenteTempoDigitalRele`
--
ALTER TABLE `CorrenteTempoDigitalRele`
  ADD PRIMARY KEY (`codigoRele`,`tipo`);

--
-- Indexes for table `CorrenteTempoDigitalReligador`
--
ALTER TABLE `CorrenteTempoDigitalReligador`
  ADD PRIMARY KEY (`codigoReligador`,`tipo`);

--
-- Indexes for table `Elo`
--
ALTER TABLE `Elo`
  ADD PRIMARY KEY (`correnteNominal`);

--
-- Indexes for table `ElotransformadorMono`
--
ALTER TABLE `ElotransformadorMono`
  ADD PRIMARY KEY (`id`),
  ADD KEY `potenciaId` (`potenciaId`),
  ADD KEY `kvId` (`kvId`);

--
-- Indexes for table `ElotransformadorTri`
--
ALTER TABLE `ElotransformadorTri`
  ADD PRIMARY KEY (`id`),
  ADD KEY `potenciaId` (`potenciaId`),
  ADD KEY `kvId` (`kvId`);

--
-- Indexes for table `KvMono`
--
ALTER TABLE `KvMono`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `KvTri`
--
ALTER TABLE `KvTri`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `PontoCurvaElo`
--
ALTER TABLE `PontoCurvaElo`
  ADD PRIMARY KEY (`id`),
  ADD KEY `correnteElo` (`correnteElo`);

--
-- Indexes for table `PontoCurvaRele`
--
ALTER TABLE `PontoCurvaRele`
  ADD PRIMARY KEY (`id`),
  ADD KEY `codigoRele` (`codigoRele`);

--
-- Indexes for table `PontoCurvaReligador`
--
ALTER TABLE `PontoCurvaReligador`
  ADD PRIMARY KEY (`id`),
  ADD KEY `codigoReligador` (`codigoReligador`);

--
-- Indexes for table `PotenciaMono`
--
ALTER TABLE `PotenciaMono`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `PotenciaTri`
--
ALTER TABLE `PotenciaTri`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `Rele`
--
ALTER TABLE `Rele`
  ADD PRIMARY KEY (`codigo`);

--
-- Indexes for table `Religador`
--
ALTER TABLE `Religador`
  ADD PRIMARY KEY (`codigo`);

--
-- Indexes for table `TempoAtuacaoDefinidoEletromecanicoRele`
--
ALTER TABLE `TempoAtuacaoDefinidoEletromecanicoRele`
  ADD PRIMARY KEY (`codigoRele`,`isFase`,`tempoAtuacao`);

--
-- Indexes for table `TempoAtuacaoDefinidoEletromecanicoReligador`
--
ALTER TABLE `TempoAtuacaoDefinidoEletromecanicoReligador`
  ADD PRIMARY KEY (`codigoReligador`,`isFase`,`tempoAtuacao`,`isRapida`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `CaracteristicasCurvaRele`
--
ALTER TABLE `CaracteristicasCurvaRele`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `CaracteristicasCurvaReligador`
--
ALTER TABLE `CaracteristicasCurvaReligador`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `ElotransformadorMono`
--
ALTER TABLE `ElotransformadorMono`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `ElotransformadorTri`
--
ALTER TABLE `ElotransformadorTri`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `PontoCurvaElo`
--
ALTER TABLE `PontoCurvaElo`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=593;
--
-- AUTO_INCREMENT for table `PontoCurvaRele`
--
ALTER TABLE `PontoCurvaRele`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- AUTO_INCREMENT for table `PontoCurvaReligador`
--
ALTER TABLE `PontoCurvaReligador`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;
--
-- Constraints for dumped tables
--

--
-- Constraints for table `CaracteristicasCurvaRele`
--
ALTER TABLE `CaracteristicasCurvaRele`
  ADD CONSTRAINT `CaracteristicasCurvaRele_ibfk_1` FOREIGN KEY (`codigoRele`) REFERENCES `Rele` (`codigo`);

--
-- Constraints for table `CaracteristicasCurvaReligador`
--
ALTER TABLE `CaracteristicasCurvaReligador`
  ADD CONSTRAINT `CaracteristicasCurvaReligador_ibfk_1` FOREIGN KEY (`codigoReligador`) REFERENCES `Religador` (`codigo`);

--
-- Constraints for table `CorrentePickupDefinidoEletromecanicoRele`
--
ALTER TABLE `CorrentePickupDefinidoEletromecanicoRele`
  ADD CONSTRAINT `CorrentePickupDefinidoEletromecanicoRele_ibfk_1` FOREIGN KEY (`codigoRele`) REFERENCES `Rele` (`codigo`);

--
-- Constraints for table `CorrentePickupDefinidoEletromecanicoReligador`
--
ALTER TABLE `CorrentePickupDefinidoEletromecanicoReligador`
  ADD CONSTRAINT `CorrentePickupDefinidoEletromecanicoReligador_ibfk_1` FOREIGN KEY (`codigoReligador`) REFERENCES `Religador` (`codigo`);

--
-- Constraints for table `CorrenteTempoDigitalRele`
--
ALTER TABLE `CorrenteTempoDigitalRele`
  ADD CONSTRAINT `CorrenteTempoDigitalRele_ibfk_1` FOREIGN KEY (`codigoRele`) REFERENCES `Rele` (`codigo`);

--
-- Constraints for table `CorrenteTempoDigitalReligador`
--
ALTER TABLE `CorrenteTempoDigitalReligador`
  ADD CONSTRAINT `CorrenteTempoDigitalReligador_ibfk_1` FOREIGN KEY (`codigoReligador`) REFERENCES `Religador` (`codigo`);

--
-- Constraints for table `ElotransformadorMono`
--
ALTER TABLE `ElotransformadorMono`
  ADD CONSTRAINT `ElotransformadorMono_ibfk_1` FOREIGN KEY (`potenciaId`) REFERENCES `PotenciaMono` (`id`),
  ADD CONSTRAINT `ElotransformadorMono_ibfk_2` FOREIGN KEY (`kvId`) REFERENCES `KvMono` (`id`);

--
-- Constraints for table `ElotransformadorTri`
--
ALTER TABLE `ElotransformadorTri`
  ADD CONSTRAINT `ElotransformadorTri_ibfk_1` FOREIGN KEY (`potenciaId`) REFERENCES `PotenciaTri` (`id`),
  ADD CONSTRAINT `ElotransformadorTri_ibfk_2` FOREIGN KEY (`kvId`) REFERENCES `KvTri` (`id`);

--
-- Constraints for table `PontoCurvaElo`
--
ALTER TABLE `PontoCurvaElo`
  ADD CONSTRAINT `PontoCurvaElo_ibfk_1` FOREIGN KEY (`correnteElo`) REFERENCES `Elo` (`correnteNominal`);

--
-- Constraints for table `PontoCurvaRele`
--
ALTER TABLE `PontoCurvaRele`
  ADD CONSTRAINT `PontoCurvaRele_ibfk_1` FOREIGN KEY (`codigoRele`) REFERENCES `Rele` (`codigo`);

--
-- Constraints for table `PontoCurvaReligador`
--
ALTER TABLE `PontoCurvaReligador`
  ADD CONSTRAINT `PontoCurvaReligador_ibfk_1` FOREIGN KEY (`codigoReligador`) REFERENCES `Religador` (`codigo`);

--
-- Constraints for table `TempoAtuacaoDefinidoEletromecanicoRele`
--
ALTER TABLE `TempoAtuacaoDefinidoEletromecanicoRele`
  ADD CONSTRAINT `TempoAtuacaoDefinidoEletromecanicoRele_ibfk_1` FOREIGN KEY (`codigoRele`) REFERENCES `Rele` (`codigo`);

--
-- Constraints for table `TempoAtuacaoDefinidoEletromecanicoReligador`
--
ALTER TABLE `TempoAtuacaoDefinidoEletromecanicoReligador`
  ADD CONSTRAINT `TempoAtuacaoDefinidoEletromecanicoReligador_ibfk_1` FOREIGN KEY (`codigoReligador`) REFERENCES `Religador` (`codigo`);

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
