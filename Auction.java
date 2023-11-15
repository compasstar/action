import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.text. *;
import java.util. *;

public class Auction {
	private static Scanner scanner = new Scanner(System.in);
	private static String username;
	private static Connection conn;
	private static Statement stmt;
	private static String sql;
	private static int userId;




	enum Category {
		ELECTRONICS, 
		BOOKS,
		HOME,
		CLOTHING,
		SPORTINGGOODS,
		OTHERS
	}
	enum Condition {
		NEW,
		LIKE_NEW,
		GOOD,
		ACCEPTABLE
	}
	enum UserType {
		NORMAL,
		ADMINISTRATOR
	}

	private static boolean LoginMenu() throws SQLException {
		String userpass, isAdmin;

		System.out.print("----< User Login >\n" +
				" ** To go back, enter 'back' in user ID.\n" +
				"     user ID: ");
		try{
			username = scanner.next();
			scanner.nextLine();

			if(username.equalsIgnoreCase("back")){
				return false;
			}

			System.out.print("     password: ");
			userpass = scanner.next();
			scanner.nextLine();
		}catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			username = null;
			return false;
		}

		/* Your code should come here to check ID and password */
		stmt = conn.createStatement();
		sql = "select id, password from users where name = '" + username + "'";
		ResultSet rs = stmt.executeQuery(sql);
		String sqlPass = null;
		while(rs.next()) {
			sqlPass = rs.getString("password");
			userId = rs.getInt("id");
			System.out.println("The correct password = " + sqlPass);
		}

		if (!userpass.equals(sqlPass)) {
			/* If Login Fails */
			System.out.println("Error: Incorrect user name or password");
			System.out.println();
			username = null;
			return false; 
		}

		System.out.println("You are successfully logged in.\n");
		return true;
	}

	private static boolean SellMenu() throws SQLException {
		String name = null;
		Category category = null;
		Condition condition = null;
		String description = null;
		char choice;
		int price;
		Timestamp timestamp = null;
		boolean flag_catg = true, flag_cond = true;


		try {
			System.out.println("----< Sell Item >\n"
					+ "---- Name of the item: ");
			name = scanner.nextLine();
		}catch (Exception e) {
			System.out.println("Error: Invalid input is entered. Going back to the previous menu.");
			return false;
		}

		do{
			System.out.println(
					"---- Choose a category.\n" +
					"    1. Electronics\n" +
					"    2. Books\n" +
					"    3. Home\n" +
					"    4. Clothing\n" +
					"    5. Sporting Goods\n" +
					"    6. Other Categories\n" +
					"    P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
			}catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			flag_catg = true;

			switch ((int) choice){
				case '1':
					category = Category.ELECTRONICS;
					continue;
				case '2':
					category = Category.BOOKS;
					continue;
				case '3':
					category = Category.HOME;
					continue;
				case '4':
					category = Category.CLOTHING;
					continue;
				case '5':
					category = Category.SPORTINGGOODS;
					continue;
				case '6':
					category = Category.OTHERS;
					continue;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_catg = false;
					continue;
			}
		}while(!flag_catg);

		do{
			System.out.println(
					"---- Select the condition of the item to sell.\n" +
					"   1. New\n" +
					"   2. Like-new\n" +
					"   3. Used (Good)\n" +
					"   4. Used (Acceptable)\n" +
					"   P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			}catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			flag_cond = true;

			switch (choice) {
				case '1':
					condition = Condition.NEW;
					break;
				case '2':
					condition = Condition.LIKE_NEW;
					break;
				case '3':
					condition = Condition.GOOD;
					break;
				case '4':
					condition = Condition.ACCEPTABLE;
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_cond = false;
					continue;
			}
		}while(!flag_cond);

		try {
			System.out.println("---- Description of the item (one line): ");
			description = scanner.nextLine();
			System.out.println("---- Buy-It-Now price: ");

			while (!scanner.hasNextInt()) {
				scanner.next();
				System.out.println("Invalid input is entered. Please enter Buy-It-Now price: ");
			}

			price = scanner.nextInt();
			scanner.nextLine();

			System.out.print("---- Bid closing date and time (YYYY-MM-DD HH:MM): ");
			// you may assume users always enter valid date/time
			String date = scanner.nextLine();  /* "2023-03-04 11:30"; */
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
			LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
			timestamp = java.sql.Timestamp.valueOf(dateTime);
		}catch (Exception e) {
			System.out.println("Error: Invalid input is entered. Going back to the previous menu.");
			return false;
		}



		/* TODO: Your code should come here to store the user inputs in your database */
		PreparedStatement pStmt = conn.prepareStatement("insert into item (name , category, condition, description, buy_it_now_price, bid_closing_date, seller_id) values (?, ?, ?, ?, ?, ?, ?)");
		pStmt.setString(1, name);
		pStmt.setString(2, String.valueOf(category));
		pStmt.setString(3, String.valueOf(condition));
		pStmt.setString(4, description);
		pStmt.setInt(5, price);
		pStmt.setTimestamp(6, timestamp);
		pStmt.setInt(7, userId);
		pStmt.executeUpdate();

		System.out.println("Your item has been successfully listed.\n");
		return true;
	}

	private static boolean SignupMenu() throws SQLException {
		/* 2. Sign Up */
		String new_username, userpass, isAdmin;
		System.out.print("----< Sign Up >\n" + 
				" ** To go back, enter 'back' in user ID.\n" +
				"---- user name: ");
		try {
			new_username = scanner.next();
			scanner.nextLine();
			if(new_username.equalsIgnoreCase("back")){
				return false;
			}
			System.out.print("---- password: ");
			userpass = scanner.next();
			scanner.nextLine();
			System.out.print("---- In this user an administrator? (Y/N): ");
			isAdmin = scanner.next();
			scanner.nextLine();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Please select again.");
			return false;
		}

		/* TODO: Your code should come here to create a user account in your database */
		PreparedStatement pStmt = conn.prepareStatement("insert into users (name, password, user_type) values (?, ?, ?)");
		pStmt.setString(1, new_username);
		pStmt.setString(2, userpass);
		if (isAdmin.equals("Y") || isAdmin.equals("y")) {
			pStmt.setString(3, "ADMINISTRATOR");
		} else {
			pStmt.setString(3, "NORMAL");
		}
		pStmt.executeUpdate();

		System.out.println("Your account has been successfully created.\n");
		return true;
	}

	private static boolean AdminMenu() throws SQLException {
		/* 3. Login as Administrator */
		char choice;
		String adminname, adminpass;
		String keyword, seller;
		System.out.print("----< Login as Administrator >\n" +
				" ** To go back, enter 'back' in user ID.\n" +
				"---- admin ID: ");

		try {
			adminname = scanner.next();
			scanner.nextLine();
			if(adminname.equalsIgnoreCase("back")){
				return false;
			}
			System.out.print("---- password: ");
			adminpass = scanner.nextLine();
			// TODO: check the admin's account and password.

			stmt = conn.createStatement();
			sql = "select id, password, user_type from users where name = '" + adminname + "'";
			ResultSet rs = stmt.executeQuery(sql);
			String sqlPass = null;
			String sqlType = null;
			while(rs.next()) {
				sqlPass = rs.getString("password");
				sqlType = rs.getString("user_type");
				userId = rs.getInt("id");
				System.out.println("The correct password = " + sqlPass);
			}


			if (!adminpass.equals(sqlPass) || !sqlType.equals("ADMINISTRATOR")) {
				/* If Login Fails */
				System.out.println("Error: Incorrect user name or password or Not Admin");
				System.out.println();
				adminname = null;
				return false;
			}

			System.out.println("You are successfully logged in.\n");

		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			return false;
		}

		boolean login_success = true;

		if(!login_success){
			// login failed. go back to the previous menu.
			return false;
		}

		do {
			System.out.println(
					"----< Admin menu > \n" +
					"    1. Print Sold Items per Category \n" +
					"    2. Print Account Balance for Seller \n" +
					"    3. Print Seller Ranking \n" +
					"    4. Print Buyer Ranking \n" +
					"    P. Go Back to Previous Menu"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			if (choice == '1') {
				System.out.println("----Enter Category to search : ");
				keyword = scanner.next();
				scanner.nextLine();
				/*TODO: Print Sold Items per Category */
				System.out.println("sold item       | sold date       | seller ID   | buyer ID   | price | commissions");
				System.out.println("----------------------------------------------------------------------------------");

				stmt = conn.createStatement();
				sql = "select i.name, b.purchase_date, s.name, u.name, b.amount_due_buyers_need_to_pay, floor(b.amount_due_buyers_need_to_pay * 0.1)" +
						" from item as i" +
						" join billing as b on b.sold_item_id = i.id" +
						" join users as s on s.id = b.seller_id" +
						" join users as u on u.id = b.buyer_id" +
						" where i.category = '" + keyword + "'";
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					System.out.println(rs.getString(1) + " | " +
							rs.getTimestamp(2) + " | " +
							rs.getString(3) + " | " +
							rs.getString(4) + " | " +
							rs.getInt(5) + " | " +
							rs.getInt(6) + " | ");
				}
				System.out.println();
				continue;
			} else if (choice == '2') {
				/*TODO: Print Account Balance for Seller */
				System.out.println("---- Enter Seller ID to search : ");
				seller = scanner.next();
				scanner.nextLine();
				System.out.println("sold item       | sold date       | buyer ID   | price | commissions");
				System.out.println("--------------------------------------------------------------------");

				stmt = conn.createStatement();
				sql = "select i.name, b.purchase_date, u.name, b.amount_due_buyers_need_to_pay, floor(b.amount_due_buyers_need_to_pay)" +
						" from item as i" +
						" join billing as b on b.sold_item_id = i.id" +
						" join users as u on u.id = b.buyer_id" +
						" join users as s on s.id = b.seller_id" +
						" where s.name = '" + seller + "'";
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					System.out.println(rs.getString(1) + " | " +
							rs.getTimestamp(2) + " | " +
							rs.getString(3) + " | " +
							rs.getInt(4) + " | " +
							rs.getInt(5) + " | ");
				}
				System.out.println();

				continue;
			} else if (choice == '3') {
				/*TODO: Print Seller Ranking */
				System.out.println("seller ID   | # of items sold | Total Profit (excluding commissions)");
				System.out.println("--------------------------------------------------------------------");

				stmt = conn.createStatement();
				sql = "select u.name, count(b.id), sum(b.amount_due_buyers_need_to_pay)" +
						" from users as u" +
						" join billing as b on u.id = b.seller_id" +
						" group by u.id" +
						" order by sum(b.amount_due_buyers_need_to_pay) desc";
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					System.out.println(rs.getString(1) + " | " +
							rs.getInt(2) + " | " +
							rs.getInt(3) + " | ");
				}
				System.out.println();

				continue;
			} else if (choice == '4') {
				/*TODO: Print Buyer Ranking */
				System.out.println("buyer ID   | # of items purchased | Total Money Spent ");
				System.out.println("------------------------------------------------------");

				stmt = conn.createStatement();
				sql = "select u.name, count(b.id), sum(b.amount_due_buyers_need_to_pay)" +
						" from users as u" +
						" join billing as b on u.id = b.buyer_id" +
						" group by u.id" +
						" order by sum(b.amount_due_buyers_need_to_pay) desc";
				ResultSet rs = stmt.executeQuery(sql);
				while (rs.next()) {
					System.out.println(rs.getString(1) + " | " +
							rs.getInt(2) + " | " +
							rs.getInt(3) + " | ");
				}
				System.out.println();

				continue;
			} else if (choice == 'P' || choice == 'p') {
				return false;
			} else {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}
		} while(true);
	}

	public static void CheckSellStatus() throws SQLException {
		/* TODO: Check the status of the item the current user is selling */

		System.out.println("item listed in Auction | bidder (buyer ID) | bidding price | bidding date/time \n");
		System.out.println("-------------------------------------------------------------------------------\n");

		stmt = conn.createStatement();
		sql = "select i.name, u.name, b.bid_price, b.date_posted" +
				" from item as i" +
				" left outer join bid as b on i.id = b.item_id" +
				" left outer join users as u on b.bidder_id = u.id" +
				" where i.seller_id = " + userId;
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			System.out.println(rs.getString(1) + " | "
					+ rs.getString(2) + " | "
					+ rs.getString(3) + " | "
					+ rs.getString(4));
		}
		System.out.println();
	}

	public static boolean BuyItem() throws SQLException {
		Category category = null;
		Condition condition = null;
		char choice;
		int price;
		String keyword, seller, datePosted;
		boolean flag_catg = true, flag_cond = true;
		
		do {
			System.out.println( "----< Select category > : \n" +
					"    1. Electronics\n"+
					"    2. Books\n" + 
					"    3. Home\n" + 
					"    4. Clothing\n" + 
					"    5. Sporting Goods\n" +
					"    6. Other categories\n" +
					"    7. Any category\n" +
					"    P. Go Back to Previous Menu"
					);
			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				return false;
			}

			flag_catg = true;

			switch (choice) {
				case '1':
					category = Category.ELECTRONICS;
					break;
				case '2':
					category = Category.BOOKS;
					break;
				case '3':
					category = Category.HOME;
					break;
				case '4':
					category = Category.CLOTHING;
					break;
				case '5':
					category = Category.SPORTINGGOODS;
					break;
				case '6':
					category = Category.OTHERS;
					break;
				case '7':
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_catg = false;
					continue;
			}
		} while(!flag_catg);

		do {

			System.out.println(
					"----< Select the condition > \n" +
					"   1. New\n" +
					"   2. Like-new\n" +
					"   3. Used (Good)\n" +
					"   4. Used (Acceptable)\n" +
					"   P. Go Back to Previous Menu"
					);
			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				return false;
			}

			flag_cond = true;

			switch (choice) {
				case '1':
					condition = Condition.NEW;
					break;
				case '2':
					condition = Condition.LIKE_NEW;
					break;
				case '3':
					condition = Condition.GOOD;
					break;
				case '4':
					condition = Condition.ACCEPTABLE;
					break;
				case 'p':
				case 'P':
					return false;
				default:
					System.out.println("Error: Invalid input is entered. Try again.");
					flag_cond = false;
					continue;
				}
		} while(!flag_cond);

		try {
			System.out.println("---- Enter keyword to search the description : ");
			keyword = scanner.next();
			scanner.nextLine();

			System.out.println("---- Enter Seller ID to search : ");
			System.out.println(" ** Enter 'any' if you want to see items from any seller. ");
			seller = scanner.next();
			scanner.nextLine();

			System.out.println("---- Enter date posted (YYYY-MM-DD): ");
			System.out.println(" ** This will search items that have been posted after the designated date.");
			datePosted = scanner.next();
			scanner.nextLine();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			return false;
		}

		/* TODO: Query condition: item category */
		/* TODO: Query condition: item condition */
		/* TODO: Query condition: items whose description match the keyword (use LIKE operator) */
		/* TODO: Query condition: items from a particular seller */
		/* TODO: Query condition: posted date of item */





		/* TODO: List all items that match the query condition */
		System.out.println("Item ID | Item description | Condition | Seller | Buy-It-Now | Current Bid | highest bidder | Time left | bid close");
		System.out.println("-------------------------------------------------------------------------------------------------------");

		stmt = conn.createStatement();
		if (seller.equals("any")) {
			sql = "select i.name, i.description, i.condition, u.name, i.buy_it_now_price, b.bid_price, s.name, concat(extract(day from age(i.bid_closing_date, current_timestamp)), ' day ', extract(hour from age(i.bid_closing_date, current_timestamp)), ' hrs') as time_difference, i.bid_closing_date"
					+ " from item as i"
					+ " left outer join (select item_id ,max(bid_price) as max_bid_price from bid group by item_id) as max_bids on i.id = max_bids.item_id"
					+ " left outer join bid as b on i.id = b.item_id and b.bid_price = max_bids.max_bid_price"
					+ " left outer join users as u on i.seller_id = u.id"
					+ " left outer join users as s on b.bidder_id = s.id"
					+ " where i.category = '" + category + "'"
					+ " and i.condition = '" + condition +"'"
					+ " and i.description like '%" + keyword + "%'"
					+ " and i.date_posted between '" + datePosted + "' and current_timestamp"
					+ " and i.bid_closing_date > current_timestamp";
		} else {
			sql = "select i.name, i.description, i.condition, u.name, i.buy_it_now_price, b.bid_price, s.name, concat(extract(day from age(i.bid_closing_date, current_timestamp)), ' day ', extract(hour from age(i.bid_closing_date, current_timestamp)), ' hrs') as time_difference, i.bid_closing_date"
					+ " from item as i"
					+ " left outer join (select item_id ,max(bid_price) as max_bid_price from bid group by item_id) as max_bids on i.id = max_bids.item_id"
					+ " left outer join bid as b on i.id = b.item_id and b.bid_price = max_bids.max_bid_price"
					+ " left outer join users as u on i.seller_id = u.id"
					+ " left outer join users as s on b.bidder_id = s.id"
					+ " where i.category = '" + category + "'"
					+ " and i.condition = '" + condition +"'"
					+ " and i.description like '%" + keyword + "%'"
					+ " and i.seller_id = " + seller
					+ " and i.date_posted between '" + datePosted + "' and current_timestamp"
					+ " and i.bid_closing_date > current_timestamp";
		}

		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			System.out.println(rs.getString(1) + " | "
					+ rs.getString(2) + " | "
					+ rs.getString(3) + " | "
					+ rs.getString(4) + " | "
					+ rs.getString(5) + " | "
					+ rs.getString(6) + " | "
					+ rs.getString(7) + " | "
					+ rs.getString(8) + " | "
					+ rs.getString(9));
		}

		System.out.println("---- Select Item ID to buy or bid: ");
		System.out.println("---- If you don't want to buy anything, write 'none'");

		String buyChoice = null;
		try {
//			choice = scanner.next().charAt(0);;
			buyChoice = scanner.nextLine();
			if (buyChoice.equals("none")) {
				return true;
			}
//			scanner.nextLine();
			System.out.println("     Price: ");
			price = scanner.nextInt();
			scanner.nextLine();
		} catch (java.util.InputMismatchException e) {
			System.out.println("Error: Invalid input is entered. Try again.");
			return false;
		}

		/* TODO: Buy-it-now or bid: If the entered price is higher or equal to Buy-It-Now price, the bid ends. */
		/* Even if the bid price is higher than the Buy-It-Now price, the buyer pays the B-I-N price. */

		int buyItNowId = 0;
		int buyItNowSellerId = 0;
		int buyItNowPrice = 999999999;

		sql = "select id, seller_id, buy_it_now_price from item where name = '" + buyChoice + "'";
		rs = stmt.executeQuery(sql);
		while(rs.next()) {
			buyItNowId = rs.getInt(1);
			buyItNowSellerId = rs.getInt(2);
			buyItNowPrice = rs.getInt(3);
		}

		if (buyItNowPrice <= price) {
			/* TODO: if you won, print the following */
			System.out.println("Congratulations, the item is yours now.\n");

			PreparedStatement pStmt = conn.prepareStatement("insert into billing (sold_item_id, seller_id, buyer_id, amount_due_buyers_need_to_pay, amount_of_money_sellers_need_to_get_paid) values (?, ?, ?, ?, ?)");
			pStmt.setInt(1, buyItNowId);
			pStmt.setInt(2, buyItNowSellerId);
			pStmt.setInt(3, userId);
			pStmt.setInt(4, buyItNowPrice);
			pStmt.setInt(5, (int) (buyItNowPrice * 0.9));
			pStmt.executeUpdate();

			PreparedStatement pStmt2 = conn.prepareStatement("update item set sold = 1 where name = '" + buyChoice + "'");
			pStmt2.executeUpdate();

		} else {
			/* TODO: if you are the current highest bidder, print the following */
			System.out.println("Congratulations, you are the highest bidder.\n");

			int bidItemId = 0;
			sql = "select id from item where name = '" + buyChoice + "'";
			rs = stmt.executeQuery(sql);
			while(rs.next()) {
				bidItemId = rs.getInt(1);
			}
			System.out.println("bidItemId = " + bidItemId);

			PreparedStatement pStmt = conn.prepareStatement("insert into bid (bidder_id, item_id, bid_price) values (?, ?, ?)");
			pStmt.setInt(1, userId);
			pStmt.setInt(2, bidItemId);
			pStmt.setInt(3, price);
			pStmt.executeUpdate();
		}

		return true;
	}

	public static void CheckBuyStatus() throws SQLException {
		/* TODO: Check the status of the item the current buyer is bidding on */
		/* Even if you are outbidded or the bid closing date has passed, all the items this user has bidded on must be displayed */
		System.out.println("item ID   | item description   | highest bidder | highest bidding price | your bidding price | bid closing date/time");
		System.out.println("--------------------------------------------------------------------------------------------------------------------");

		stmt = conn.createStatement();
		sql = "select i.name, i.description, u.name, b.bid_price, d.bid_price, i.bid_closing_date" +
				" from item as i" +
				" join (" +
				"select item_id, max(bid_price) as max_bid_price" +
				" from bid" +
				" group by item_id" +
				") as max_bids on i.id = max_bids.item_id" +
				" join bid as b on i.id = b.item_id and b.bid_price = max_bids.max_bid_price" +
				" join bid as d on i.id = d.item_id" +
				" join users as u on b.bidder_id = u.id" +
				" where d.bidder_id = " + userId;
		ResultSet rs = stmt.executeQuery(sql);
		while(rs.next()) {
			System.out.println(rs.getString(1) + " | " +
							rs.getString(2) + " | " +
							rs.getString(3) + " | " +
							rs.getString(4) + " | " +
							rs.getString(5) + " | " +
							rs.getString(6));
		}


	}

	public static void CheckAccount() throws SQLException {

		int soldItemId = 0;
		Timestamp purchaseDate = null;
		int sellerId = 0;
		int buyerId = 0;
		int amountDueBuyersNeedToPay = 0;
		int amountOfMoneySellersNeedToGetPaid = 0;

		// find the items that are out of bid_closing_time
		stmt = conn.createStatement();
		sql = "select i.id, i.bid_closing_date, i.seller_id, b.bidder_id, b.bid_price, floor(b.bid_price * 0.9)" +
				" from item as i" +
				" join (" +
				" select item_id, max(bid_price) as max_bid_price" +
				" from bid" +
				" group by item_id" +
				" ) as max_bids on i.id = max_bids.item_id" +
				" join bid as b on i.id = b.item_id and b.bid_price = max_bids.max_bid_price" +
				" where i.bid_closing_date < current_date and i.sold = 0;";
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next()) {
			soldItemId = rs.getInt(1);
			purchaseDate = rs.getTimestamp(2);
			sellerId = rs.getInt(3);
			buyerId = rs.getInt(4);
			amountDueBuyersNeedToPay = rs.getInt(5);
			amountOfMoneySellersNeedToGetPaid = rs.getInt(6);


			// insert into billing for out of bid_closing_time
			String sql2 = "insert into billing (sold_item_id, purchase_date, seller_id, buyer_id, amount_due_buyers_need_to_pay, amount_of_money_sellers_need_to_get_paid) values (?, ?, ?, ?, ?, ?)";
			PreparedStatement pStmt = conn.prepareStatement(sql2);
			pStmt.setInt(1, soldItemId);
			pStmt.setTimestamp(2, purchaseDate);
			pStmt.setInt(3, sellerId);
			pStmt.setInt(4, buyerId);
			pStmt.setInt(5, amountDueBuyersNeedToPay);
			pStmt.setInt(6, amountOfMoneySellersNeedToGetPaid);
			pStmt.executeUpdate();

			//update items sold = 1 that means the item is sold
			PreparedStatement pStmt2 = conn.prepareStatement("update item set sold = 1 where id = " + soldItemId);
			pStmt2.executeUpdate();
		}


		/* TODO: Check the balance of the current user.  */
		System.out.println("[Sold Items] \n");
		System.out.println("item category  | item ID   | sold date | sold price  | buyer ID | commission  ");
		System.out.println("------------------------------------------------------------------------------");

		stmt = conn.createStatement();
		String sqlSoldItems = "select i.category, i.name, b.purchase_date, b.amount_due_buyers_need_to_pay, u.name, floor(b.amount_due_buyers_need_to_pay * 0.1)" +
				" from item as i" +
				" join billing as b on i.id = b.sold_item_id" +
				" join users as u on b.buyer_id = u.id" +
				" where i.seller_id = " + userId;
		ResultSet rsSoldItems = stmt.executeQuery(sqlSoldItems);
		while (rsSoldItems.next()) {
			System.out.println(rsSoldItems.getString(1) + " | " +
					rsSoldItems.getString(2) + " | " +
					rsSoldItems.getTimestamp(3) + " | " +
					rsSoldItems.getInt(4) + " | " +
					rsSoldItems.getString(5) + " | " +
					rsSoldItems.getInt(6) + " | ");
		}

		System.out.println();

		System.out.println("[Purchased Items] \n");
		System.out.println("item category  | item ID   | purchased date | puchased price  | seller ID ");
		System.out.println("--------------------------------------------------------------------------");

		stmt = conn.createStatement();
		String sqlPurchasedItems = "select i.category, i.name, b.purchase_date, b.amount_due_buyers_need_to_pay, u.name" +
				" from item as i" +
				" join billing as b on i.id = b.sold_item_id" +
				" join users as u on b.seller_id = u.id" +
				" where b.buyer_id = " + userId;
		ResultSet rsPurchasedItems = stmt.executeQuery(sqlPurchasedItems);
		while (rsPurchasedItems.next()) {
			System.out.println(rsPurchasedItems.getString(1) + " | " +
					rsPurchasedItems.getString(2) + " | " +
					rsPurchasedItems.getTimestamp(3) + " | " +
					rsPurchasedItems.getInt(4) + " | " +
					rsPurchasedItems.getString(5) + " | ");
		}

	}

	public static void main(String[] args) {
		char choice;
		boolean ret;

		if(args.length<2){
			System.out.println("Usage: java Auction postgres_id password");
			System.exit(1);
		}

		/**
		 * 인텔리제이 자바 실행용
		 */
		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("PostgreSQL JDBC Diver not Found!");
			e.printStackTrace();
			return;
		}
		System.out.println("PostgreSQL JDBC Driver Registered!");

		try{
                	conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/"+args[0], args[1], args[2]);
//            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db18310870", "db18310870", "changethis");
		}
		catch(SQLException e){
			System.out.println("SQLException : " + e);	
			System.exit(1);
		}

		do {
			username = null;
			System.out.println(
					"----< Login menu >\n" + 
					"----(1) Login\n" +
					"----(2) Sign up\n" +
					"----(3) Login as Administrator\n" +
					"----(Q) Quit"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			try {
				switch ((int) choice) {
					case '1':
						ret = LoginMenu();
						if(!ret) continue;
						break;
					case '2':
						ret = SignupMenu();
						if(!ret) continue;
						break;
					case '3':
						ret = AdminMenu();
						if(!ret) continue;
					case 'q':
					case 'Q':
						System.out.println("Good Bye");
						/* TODO: close the connection and clean up everything here */
						conn.close();
						System.exit(1);
					default:
						System.out.println("Error: Invalid input is entered. Try again.");
				}
			} catch (SQLException e) {
				System.out.println("SQLException : " + e);	
			}
		} while (username==null || username.equalsIgnoreCase("back"));  

		// logged in as a normal user 
		do {
			System.out.println(
					"---< Main menu > :\n" +
					"----(1) Sell Item\n" +
					"----(2) Status of Your Item Listed on Auction\n" +
					"----(3) Buy Item\n" +
					"----(4) Check Status of your Bid \n" +
					"----(5) Check your Account \n" +
					"----(Q) Quit"
					);

			try {
				choice = scanner.next().charAt(0);;
				scanner.nextLine();
			} catch (java.util.InputMismatchException e) {
				System.out.println("Error: Invalid input is entered. Try again.");
				continue;
			}

			try{
				switch (choice) {
					case '1':
						ret = SellMenu();
						if(!ret) continue;
						break;
					case '2':
						CheckSellStatus();
						break;
					case '3':
						ret = BuyItem();
						if(!ret) continue;
						break;
					case '4':
						CheckBuyStatus();
						break;
					case '5':
						CheckAccount();
						break;
					case 'q':
					case 'Q':
						System.out.println("Good Bye");
						/* TODO: close the connection and clean up everything here */
						stmt.close();
						conn.close();
						System.exit(1);
				}
			} catch (SQLException e) {
				System.out.println("SQLException : " + e);	
				System.exit(1);
			}
		} while(true);
	} // End of main 
} // End of class


