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
			if(customer.getCustumerId() == customerId) customerRepository2.delete(customer);
		}

	}

	@Override
	public TripBooking bookTrip(int customerId, String fromLocation, String toLocation, int distanceInKm) throws Exception{
		//Book the driver with lowest driverId who is free (cab available variable is Boolean.TRUE). If no driver is available, throw "No cab available!" exception
		//Avoid using SQL query

//		Optional<Customer> optionalCustomer = customerRepository2.findById(customerId);
//		if(optionalCustomer.isPresent()){
//
//		}
		List<Driver> list = driverRepository2.findAll();
		boolean flag = false;
		for(Driver driver : list){
			if(driver.getDriverId()>0 && driver.getCab().isAvailable())  {
				driver.getCab().setAvailable(false);
				flag = true;
				break;
			}
		}
		if(flag==false) throw new Exception("No cab available!");
     TripBooking tripBooking = new TripBooking();
	 tripBooking.setFromLocation(fromLocation);
	 tripBooking.setToLocation(toLocation);
	 tripBooking.setDistanceinkm(distanceInKm);
	 tripBooking.setTripStatus(TripStatus.CONFIRMED);


 	 TripBooking tripBooking1 = tripBookingRepository2.save(tripBooking);

		Optional<Customer> optionalcustomer = customerRepository2.findById(customerId);
	 if(optionalcustomer.isPresent()){
		 Customer customer = optionalcustomer.get();
		 customer.getTripBookingList().add(tripBooking1);
 }
	 return tripBooking1;
	}

	@Override
	public void cancelTrip(Integer tripId){
		//Cancel the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBooking = tripBookingRepository2.findById(tripId);
		if(tripBooking.isPresent()){
			TripBooking trip = tripBooking.get();
			trip.setTripStatus(TripStatus.CANCELED);
		}

	}

	@Override
	public void completeTrip(Integer tripId){
		//Complete the trip having given trip Id and update TripBooking attributes accordingly
		Optional<TripBooking> tripBooking = tripBookingRepository2.findById(tripId);
		if(tripBooking.isPresent()){
			TripBooking trip = tripBooking.get();
			trip.setTripStatus(TripStatus.COMPLETED);
		}
	}
}
