package staysplit.hotel_reservation.customer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import staysplit.hotel_reservation.common.exception.AppException;
import staysplit.hotel_reservation.common.exception.ErrorCode;
import staysplit.hotel_reservation.customer.domain.dto.request.CustomerSignupRequest;
import staysplit.hotel_reservation.customer.domain.dto.request.NicknameChangeRequest;
import staysplit.hotel_reservation.customer.domain.entity.CustomerEntity;
import staysplit.hotel_reservation.customer.domain.dto.response.CustomerDetailsResponse;
import staysplit.hotel_reservation.customer.repository.CustomerRepository;
import staysplit.hotel_reservation.user.domain.entity.UserEntity;
import staysplit.hotel_reservation.user.domain.enums.LoginSource;
import staysplit.hotel_reservation.user.domain.enums.Role;
import staysplit.hotel_reservation.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public CustomerDetailsResponse signup(CustomerSignupRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new AppException(ErrorCode.DUPLICATE_EMAIL, ErrorCode.DUPLICATE_EMAIL.getMessage());
        }

        if (customerRepository.existsByNickname(request.nickname())) {
            throw new AppException(ErrorCode.DUPLICATE_NICKNAME, ErrorCode.DUPLICATE_NICKNAME.getMessage());
        }

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.CUSTOMER)
                .loginSource(LoginSource.LOCAL)
                .build();

        userRepository.save(user);

        CustomerEntity customer = CustomerEntity.builder()
                .user(user)
                .name(request.name())
                .birthdate(request.birthdate())
                .nickname(request.nickname())
                .build();

        customerRepository.save(customer);
        return CustomerDetailsResponse.from(customer);
    }

    // 내 프로필
    public CustomerDetailsResponse getMyProfile(String email) {
        CustomerEntity customer = validateCustomer(email);
        return CustomerDetailsResponse.from(customer);
    }

    public CustomerDetailsResponse findCustomerById(Long id) {
        CustomerEntity customer = validateCustomer(id);
        return CustomerDetailsResponse.from(customer);
    }

    public CustomerDetailsResponse changeNickname(NicknameChangeRequest request, String email) {
        CustomerEntity customer = validateCustomer(email);
        if (customerRepository.existsByNickname(request.getNickname())) {
            throw new AppException(ErrorCode.DUPLICATE_NICKNAME, ErrorCode.DUPLICATE_NICKNAME.getMessage());
        }
        customer.setNickname(request.getNickname());
        return CustomerDetailsResponse.from(customer);
    }

//    // 두번째 이메일은 Authentication 객체에 있는 값
//    public CustomerInfoResponse changeEmail(EmailChangeRequest emailChangeRequest, String email) {
//        CustomerEntity customer = validateCustomer(email);
//        if (customerRepository.existsByEmail(emailChangeRequest.getEmail())) {
//            throw new AppException(ErrorCode.DUPLICATE_EMAIL, ErrorCode.DUPLICATE_EMAIL.getMessage());
//        }
//        customer.changeEmail(emailChangeRequest.getEmail());
//        return CustomerInfoResponse.from(customer);
//    }


    public void delete(String email) {
        CustomerEntity customer = validateCustomer(email);
        customerRepository.delete(customer);
    }

    public Page<CustomerDetailsResponse> getAllCustomers(Pageable pageable) {
        Page<CustomerEntity> all = customerRepository.findAll(pageable);
        return all.map(CustomerDetailsResponse::from);
    }

    private CustomerEntity validateCustomer(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));
    }

    private CustomerEntity validateCustomer(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));

        return customerRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND, ErrorCode.USER_NOT_FOUND.getMessage()));

    }

}
