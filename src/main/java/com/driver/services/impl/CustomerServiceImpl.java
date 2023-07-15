package com.driver.services.impl;

import com.driver.model.TripBooking;
import com.driver.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.driver.model.Customer;
import com.driver.model.Driver;
import com.driver.repository.CustomerRepository;
import com.driver.repository.DriverRepository;
import com.driver.repository.TripBookingRepository;
import com.driver.model.TripStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository2;

	@Autowired
	DriverRepository driverRepository2;

	@Autowired
	TripBookingRepository tripBookingRepository2;

	@Override
	public void register(Customer customer) {
		//Save the customer in database
		customerRepository2.save(customer);
	}

	@Override
	public void deleteCustomer(Integer customerId) {
		// Delete customer without using deleteById function
		List<Customer> list = customerRepository2.findAll();
		for(Customer customer : list){
			if(customer.getCustomerId() == customerId) customerRepository2.delete(customer);
		}

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query
//
		Customer customer = customerRepository2.findById(customerId).get();
//		if(optionalCustomer.isPresent()){
//
//			TripBooking tripBooking = new TripBooking();
//			tripBooking.setToLocation(toLocation);
//			tripBooking.setFromLocation(fromLocation);
//			tripBooking.setDistanceInKm(distanceInKm);
//
//		}
		List<Driver> list = driverRepository2.findAll();
		boolean flag = false;
		int num = Integer.MAX_VALUE;
		int price = 1;
		for(Driver driver : list){
			if(driver.getDriverId()<num && driver.getCab().getAvailable() == true)  {
				num = driver.getDriverId();
				driver.getCab().setAvailable(false);
				price = driver.getCab().getPerKmRate();
				flag = true;

			}
		}
		if(flag==false) throw new Exception("No cab available!");
     TripBooking tripBooking = new TripBooking();
	 tripBooking.setFromLocation(fromLocation);
	 tripBooking.setToLocation(toLocation);
	 tripBooking.setDistanceInKm(distanceInKm);
	 tripBooking.setStatus(TripStatus.CONFIRMED);
	 tripBooking.setBill(distanceInKm*price);


 	 TripBooking tripBooking1 = tripBookingRepository2.save(tripBooking);

		Optional<Customer> optionalcustomer = customerRepository2.findById(customerId);
	 if(optionalcustomer.isPresent()){
		 Customer cus = optionalcustomer.get();
		 cus.getTripBookingList().add(tripBooking1);
 }
	 return tripBooking1;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBooking = tripBookingRepository2.findById(tripId);
		if(tripBooking.isPresent()){
			TripBooking trip = tripBooking.get();
			trip.setStatus(TripStatus.CANCELED);
			tripBookingRepository2.delete(trip);

		}

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBooking = tripBookingRepository2.findById(tripId);
		if(tripBooking.isPresent()){
			TripBooking trip = tripBooking.get();
			trip.setStatus(TripStatus.COMPLETED);
			tripBookingRepository2.save(trip);
		}
	}
}
