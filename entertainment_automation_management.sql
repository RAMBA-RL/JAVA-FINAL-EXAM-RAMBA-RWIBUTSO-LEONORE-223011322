SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `entertainment_automation`
--

--
-- Dumping data for table `events`
--

INSERT INTO `events` (`EventID`, `ReferenceID`, `Title`, `Description`, `EventDate`, `VenueID`, `Status`, `TicketPrice`, `AvailableTickets`, `CreatedBy`, `Remarks`, `CreatedAt`) VALUES
(1, 'KGL2024001', 'New Year Groove', 'Annual jazz festival featuring international and local artists', '2026-01-01 00:00:00', 1, 'Scheduled', 15000.00, 100, 2, 'Main event', '2025-10-27 09:21:02'),
(2, 'KGL2024002', 'Rwanda Cultural Festival', 'Celebration of Rwandan culture and traditions', '2026-07-01 00:00:00', 4, 'Scheduled', 5000.00, 399, 2, 'Family friendly event', '2025-10-27 09:21:02'),
(6, 'KGL2024005', 'Iwacu muzika festival', 'Enjoy music', '2024-12-31 18:00:00', 3, 'Completed', 3000.00, 119, 1, 'Let be there', '2025-10-28 09:03:30'),
(13, 'KGL2026003', 'Inyamibwa Concert', 'Umuco Iwacu', '2026-02-27 00:00:00', 1, 'Scheduled', 5000.00, 498, 1, 'Ntuzabure!', '2025-11-05 11:53:56'),
(7, 'KGL20242001', 'Silver Gala', '', '2024-12-31 00:00:00', 1, 'Completed', 40000.00, 0, 1, '', '2025-11-03 11:11:08'),
(14, '2025004', 'Alarm Ministries Concert', 'Worshipping is key', '2025-11-11 18:00:00', 6, 'Completed', 5000.00, 39, 1, 'Let be there', '2025-11-08 17:10:04'),
(15, 'KGL2026009', 'Icyambu Concert', 'Worship', '2026-01-01 18:00:00', 1, 'Scheduled', 10000.00, 400, 1, '', '2025-11-22 11:57:43'),
(16, 'HUY2026004', 'SUMMER LOGIN', 'SUMMER', '2026-01-02 18:00:00', 1, 'Scheduled', 7000.00, 600, 1, '', '2025-11-22 12:00:45');

--
-- Dumping data for table `event_sponsors`
--

INSERT INTO `event_sponsors` (`EventID`, `SponsorID`, `Contribution`) VALUES
(1, 1, 2000000.00),
(1, 2, 1000000.00),
(2, 3, 800000.00),
(3, 1, 1500000.00),
(4, 4, 500000.00);

--
-- Dumping data for table `reviews`
--

INSERT INTO `reviews` (`ReviewID`, `EventID`, `UserID`, `Rating`, `Comment`, `ReviewDate`) VALUES
(1, 1, 4, 5, 'Excellent organization and great performances! Looking forward to next year.', '2025-10-27 09:22:36'),
(2, 2, 4, 4, 'Wonderful cultural experience, but seating could be improved.', '2025-10-27 09:22:36'),
(3, 1, 2, 2, 'Wonderful', '2025-10-28 15:11:43'),
(4, 1, 2, 1, 'gggg', '2025-10-29 15:18:29'),
(5, 1, 1, 4, 'fSound was not good at all', '2025-11-04 08:07:43'),
(6, 1, 1, 3, 'Good', '2025-11-04 14:47:32'),
(7, 6, 4, 4, 'WOW', '2025-11-04 15:09:04'),
(8, 2, 1, 4, 'They loved the event', '2025-11-05 12:10:59'),
(9, 6, 5, 5, 'It was very good festival', '2025-11-05 12:19:51'),
(10, 7, 5, 5, 'Wow', '2025-11-05 12:21:03'),
(11, 6, 5, 1, '', '2025-11-08 17:55:08'),
(12, 6, 5, 3, 'It was great', '2025-11-22 12:25:18');

--
-- Dumping data for table `sponsors`
--

INSERT INTO `sponsors` (`SponsorID`, `CompanyName`, `ContactPerson`, `Email`, `Phone`, `SponsorshipLevel`, `Amount`, `CreatedAt`) VALUES
(1, 'MTN Rwanda', 'David Kamanzi', 'sponsorship@mtn.rw', '+250788111111', 'Platinum', 5000000.00, '2025-10-27 09:20:48'),
(2, 'Bank of Kigali', 'Sarah Mutoni', 'marketing@bk.rw', '+250788222222', 'Gold', 3000000.00, '2025-10-27 09:20:48'),
(3, 'RwandAir', 'James Nkusi', 'corporate@rwandair.com', '+250788333333', 'Silver', 1500000.00, '2025-10-27 09:20:48'),
(4, 'Inyange Industries', 'Marie Aimee', 'info@inyangeindustries.rw', '+250788444444', 'Bronze', 800000.00, '2025-10-27 09:20:48'),
(5, 'Airtel ', 'Nelly Afasno', 'nelly@airtel.rw', '+25078888888', 'Silver', 4000.00, '2025-10-27 20:05:38'),
(6, 'Equity Bank', 'Jolly Senga', 'jolly@equity.rw', '+2507939932336', 'Platinum', 1000000.00, '2025-11-05 12:09:26'),
(7, 'Inyange Industries', 'Rwibutso Levi', 'levi@inyange.rw', '+250788999150', 'Gold', 300000.00, '2025-11-08 17:37:41'),
(8, 'Skol Rwanda', 'Ange Imena', 'ange@skol.rw', '+250789901023', 'Silver', 20000000.00, '2025-11-22 11:52:16');

--
-- Dumping data for table `tickets`
--

INSERT INTO `tickets` (`TicketID`, `EventID`, `UserID`, `TicketNumber`, `SeatNumber`, `TicketType`, `Price`, `Status`, `PurchaseDate`, `TicketTypeID`) VALUES
(1, 1, 4, 'TKT001', 'A101', 'VIP', 15000.00, 'Active', '2025-10-27 09:22:34', NULL),
(2, 1, 4, 'TKT002', 'A102', 'VIP', 15000.00, 'Active', '2025-10-27 09:22:34', NULL),
(3, 2, 4, 'TKT003', 'G50', 'Regular', 5000.00, 'Active', '2025-10-27 09:22:34', NULL),
(4, 6, 4, 'TKT1762171799283', 'G02', 'VIP', 3000.00, 'Active', '2025-11-03 12:09:59', NULL),
(5, 4, 4, 'TKT1762173452256', 'G45', 'VIP', 30000.00, 'Active', '2025-11-03 12:37:32', NULL),
(6, 8, 4, 'TKT1762180792996', 'G5', 'Regular', 45000.00, 'Active', '2025-11-03 14:39:53', NULL),
(7, 1, 4, 'TKT1762180867590', 'G4', 'Regular', 15000.00, 'Active', '2025-11-03 14:41:08', NULL),
(8, 1, 4, 'TKT-6F1CE150', 'G 10', 'VIP', 10000.00, 'Active', '2025-11-03 19:16:08', 3),
(9, 1, 4, 'TKT-92D6A7AF', 'G22', 'Regular', 2000.00, 'Active', '2025-11-03 19:30:57', 1),
(10, 8, 4, 'TKT-CC967FF6', 'G23', 'VIP', 7000.00, 'Active', '2025-11-03 19:36:33', 15),
(11, 1, 4, 'TKT-6730014A', 'G3', 'Regular', 2000.00, 'Active', '2025-11-04 07:27:37', 4),
(12, 2, 5, 'TKT-FDB092A4', 'VIP2', 'VIP', 7000.00, 'Active', '2025-11-04 08:06:33', 11),
(13, 13, 5, 'TKT-A79E88E1', 'V01', 'VIP', 20000.00, 'Active', '2025-11-05 12:17:40', 29),
(14, 6, 5, 'TKT-F2D07C32', 'G6', 'Regular', 2000.00, 'Active', '2025-11-06 12:51:40', 7),
(15, 13, 5, 'TKT-97808A66', 'I7', 'Regular', 5000.00, 'Active', '2025-11-06 12:55:33', 28),
(16, 6, 4, 'TKT-38C3BAB6', 'G5', 'Regular', 2000.00, 'Active', '2025-11-08 17:47:20', 7),
(17, 14, 4, 'TKT-01939BBF', 'S2', 'Regular', 5000.00, 'Active', '2025-11-08 17:47:59', 30),
(18, 2, 5, 'TKT-FF7BC146', 'S1', 'VIP', 7000.00, 'Active', '2025-11-20 11:40:47', 11),
(19, 14, 5, 'TKT-4E7AE669', 'S1', 'Regular', 5000.00, 'Active', '2025-11-22 12:17:15', 30);

--
-- Dumping data for table `tickettypes`
--

INSERT INTO `tickettypes` (`TicketTypeID`, `EventID`, `TypeName`, `Price`, `Description`) VALUES
(4, 1, 'REGULAR', 2000.00, 'Standard admission'),
(5, 2, 'REGULAR', 2000.00, 'Standard admission'),
(6, 4, 'REGULAR', 2000.00, 'Standard admission'),
(7, 6, 'REGULAR', 2000.00, 'Standard admission'),
(8, 7, 'REGULAR', 2000.00, 'Standard admission'),
(9, 8, 'REGULAR', 2000.00, 'Standard admission'),
(10, 1, 'VIP', 7000.00, 'Premium seating and benefits'),
(11, 2, 'VIP', 7000.00, 'Premium seating and benefits'),
(12, 4, 'VIP', 7000.00, 'Premium seating and benefits'),
(13, 6, 'VIP', 7000.00, 'Premium seating and benefits'),
(14, 7, 'VIP', 7000.00, 'Premium seating and benefits'),
(15, 8, 'VIP', 7000.00, 'Premium seating and benefits'),
(16, 1, '', 10000.00, 'Exclusive access and premium experience'),
(17, 2, '', 10000.00, 'Exclusive access and premium experience'),
(18, 4, '', 10000.00, 'Exclusive access and premium experience'),
(19, 6, '', 10000.00, 'Exclusive access and premium experience'),
(20, 7, '', 10000.00, 'Exclusive access and premium experience'),
(21, 8, '', 10000.00, 'Exclusive access and premium experience'),
(22, 10, 'REGULAR', 3000.00, NULL),
(23, 10, 'VIP', 7000.00, NULL),
(24, 11, 'REGULAR', 4000.00, 'Standard seating'),
(25, 11, 'VIP', 8000.00, 'Premium seating with extra benefits'),
(26, 12, 'REGULAR', 600.00, 'Standard seating'),
(27, 12, 'VIP', 900.00, 'Premium seating with extra benefits'),
(28, 13, 'REGULAR', 5000.00, 'Standard seating'),
(29, 13, 'VIP', 20000.00, 'Premium seating with extra benefits'),
(30, 14, 'REGULAR', 5000.00, 'Standard seating'),
(31, 14, 'VIP', 15000.00, 'Premium seating with extra benefits'),
(32, 15, 'REGULAR', 10000.00, 'Standard seating'),
(33, 15, 'VIP', 25000.00, 'Premium seating with extra benefits'),
(34, 16, 'REGULAR', 7000.00, 'Standard seating'),
(35, 16, 'VIP', 14000.00, 'Premium seating with extra benefits');

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`UserID`, `Username`, `PasswordHash`, `Email`, `FullName`, `Role`, `CreatedAt`, `LastLogin`) VALUES
(1, 'admin', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'admin@entertainment.rw', 'Administrator Ramba', 'Administrator', '2025-10-27 09:20:26', '2025-12-17 09:15:46'),
(2, 'Rwibutso', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'manager@kglive.rw', 'Kigali Events Manager', 'EventManager', '2025-10-27 09:20:26', '2025-10-29 15:17:46'),
(3, 'venue1', 'password', 'venue@bkarena.rw', 'BK Arena Manager', 'VenueManager', '2025-10-27 09:20:26', '2025-11-03 20:01:40'),
(4, 'Ramba', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'client@yahoo.com', 'Ramba Rwibutso L', 'Customer', '2025-10-27 09:20:26', '2025-11-08 17:42:52'),
(5, 'esther', '03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4', 'esther@customer.com', 'Esther Mugeni', 'Customer', '2025-11-04 08:05:30', '2025-12-13 10:50:08');

--
-- Dumping data for table `venues`
--

INSERT INTO `venues` (`VenueID`, `Name`, `Address`, `Capacity`, `Manager`, `Contact`, `CreatedAt`) VALUES
(1, 'BK Arena', 'KG 9 Ave, Kigali', 10000, 'John Mugisha', '+250788123456', '2025-10-27 09:20:39'),
(2, 'Kigali Convention Centre', 'KG 2 Ave, Kigali', 2600, 'Alice Uwase', '+250788654321', '2025-10-27 09:20:39'),
(3, 'Amahoro Stadium', 'Remera, Kigali', 30000, 'Robert Habimana', '+250788789012', '2025-10-27 09:20:39'),
(4, 'Kigali Cultural Village', 'Nyarugenge, Kigali', 1500, 'Grace Ingabire', '+250788345678', '2025-10-27 09:20:39'),
(5, 'Kigali Conference and Exhibition Village', 'KN 3 Ave,Kigali', 5000, 'City of Kigali', '+250780108150', '2025-11-05 12:05:30'),
(6, 'Intare Conference Arena', 'Gasabo, Kigali', 400, 'Gasabo District', '+250788445678', '2025-11-08 07:44:51'),
(7, 'Regionalle Nyamirambo', 'Nyarugenge,Kigali', 10000, 'City of Kigali', '+250788901111', '2025-11-08 17:30:52'),
(8, 'HUYE CAMPUS STADIUM', 'Huye, South', 4000, 'UR HUYE', '+25078990012', '2025-11-22 12:07:35');

--
-- Dumping data for table `venue_sponsors`
--

INSERT INTO `venue_sponsors` (`VenueID`, `SponsorID`, `SponsorshipStart`, `SponsorshipEnd`) VALUES
(1, 1, '2025-02-01', '2025-12-31'),
(2, 2, '2024-03-01', '2024-09-30'),
(1, 2, '2025-01-01', '2025-12-31'),
(2, 1, '2025-01-01', '2025-12-31');
COMMIT;

