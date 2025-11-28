package com.ra.base_spring_boot.services.impl;

import com.ra.base_spring_boot.config.VNPayProperties;
import com.ra.base_spring_boot.config.VNPayUtil;
import com.ra.base_spring_boot.model.*;
import com.ra.base_spring_boot.model.constants.RoleName;
import com.ra.base_spring_boot.repository.ICandidateRepository;
import com.ra.base_spring_boot.repository.IAccountCompanyRepository;
import com.ra.base_spring_boot.repository.PaymentTransactionRepository;
import com.ra.base_spring_boot.services.VNPayService;
import com.ra.base_spring_boot.services.IRoleService;
import com.ra.base_spring_boot.services.EmailService;
import com.ra.base_spring_boot.dto.resp.TransactionResponseDTO; // Import DTO mới
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors; // Cần thiết cho DTO conversion

@Service
@Transactional
public class VNPayServiceImpl implements VNPayService {

    private final VNPayProperties vnpayProperties;
    private final ICandidateRepository candidateRepository;
    private final PaymentTransactionRepository transactionRepository;
    private final IAccountCompanyRepository accountCompanyRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRoleService roleService;
    private final EmailService emailService;


    public VNPayServiceImpl(VNPayProperties vnpayProperties,
                            ICandidateRepository candidateRepository,
                            PaymentTransactionRepository transactionRepository,
                            IAccountCompanyRepository accountCompanyRepository,
                            PasswordEncoder passwordEncoder,
                            IRoleService roleService,
                            EmailService emailService) {
        this.vnpayProperties = vnpayProperties;
        this.candidateRepository = candidateRepository;
        this.transactionRepository = transactionRepository;
        this.accountCompanyRepository = accountCompanyRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    // --- PHƯƠNG THỨC MỚI CHO ADMIN START ---

    /**
     * Lấy tất cả giao dịch và chuyển đổi sang DTO cho Admin.
     */
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactionsForAdmin() {
        List<PaymentTransaction> transactions = transactionRepository.findAll();

        return transactions.stream()
                .filter(transaction -> !"PENDING".equals(transaction.getTransactionStatus()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }


    private TransactionResponseDTO convertToDto(PaymentTransaction transaction) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        Date premiumDate = null; // Khởi tạo biến lưu ngày hết hạn

        // 1. Sao chép các trường trực tiếp
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setBankCode(transaction.getBankCode());
        dto.setTransactionStatus(transaction.getTransactionStatus());
        dto.setVnpayOrderInfo(transaction.getVnpayOrderInfo());
        dto.setVnpayTxnRef(transaction.getVnpayTxnRef());
        dto.setPaymentDate(transaction.getPaymentDate());


        premiumDate = transaction.getPremiumUntil();


        if (transaction.getSubscriptionPlan() != null) {
            dto.setSubscriptionPlanName(transaction.getSubscriptionPlan().getName());
            dto.setSubscriptionDurationDays(transaction.getSubscriptionPlan().getDurationInDays());
        } else {
            dto.setSubscriptionPlanName("N/A");
            dto.setSubscriptionDurationDays(0);
        }

        // 3. Xác định loại người dùng và ID/Identifier
        if (transaction.getCandidate() != null) {
            dto.setUserType("Candidate");
            dto.setUserIdentifier(transaction.getCandidate().getName());
            dto.setUserId(transaction.getCandidate().getId());

            // Nếu PremiumUntil của Transaction NULL, lấy từ Candidate
            if (premiumDate == null) {
                premiumDate = transaction.getCandidate().getPremiumUntil();
            }

        } else if (transaction.getAccountCompany() != null) {
            dto.setUserType("Company");
            // Lấy tên công ty
            String companyName = transaction.getAccountCompany().getCompany() != null
                    ? transaction.getAccountCompany().getCompany().getName()
                    : transaction.getAccountCompany().getFullName(); // Fallback

            dto.setUserIdentifier(companyName);
            dto.setUserId(transaction.getAccountCompany().getId());

            // Nếu PremiumUntil của Transaction NULL, lấy từ AccountCompany
            if (premiumDate == null) {
                premiumDate = transaction.getAccountCompany().getPremiumUntil();
            }

        } else {
            dto.setUserType("Unknown");
            dto.setUserIdentifier("N/A");
            dto.setUserId(null);
        }

        // Gán giá trị PremiumUntil cuối cùng (có thể là NULL, từ Transaction, hoặc từ User Entity)
        dto.setPremiumUntil(premiumDate);

        return dto;
    }
    // --- PHƯƠNG THỨC MỚI CHO ADMIN END ---


    @Transactional
    public void createExtraAccounts(AccountCompany principalAccount, Company company) {

        Role subRole = roleService.findByRoleName(RoleName.ROLE_COMPANY);
        Set<Role> roles = Collections.singleton(subRole);

        String basePassword = UUID.randomUUID().toString().substring(0, 8);
        String encodedPassword = passwordEncoder.encode(basePassword);

        emailService.sendNewSubAccountCredentials(principalAccount.getEmail(), basePassword);

        for (int i = 1; i <= 3; i++) {
            String subEmail = String.format("sub_%d_%s", i, principalAccount.getEmail());

            AccountCompany subAccount = AccountCompany.builder()
                    .fullName("Sub Account " + i + " - " + principalAccount.getFullName())
                    .email(subEmail)
                    .password(encodedPassword)
                    .roles(roles)
                    .company(company)
                    .status(true)
                    .isPremium(true)
                    .premiumUntil(principalAccount.getPremiumUntil())
                    .build();

            accountCompanyRepository.save(subAccount);
        }
    }


    @Override
    public String createPaymentUrl(HttpServletRequest request, BigDecimal amount, String orderInfo, String vnpayTxnRef) throws UnsupportedEncodingException {
        long vnp_Amount = amount.multiply(new BigDecimal(100)).longValue();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnpayProperties.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(vnp_Amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnpayTxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpayProperties.getReturnUrl());

        String vnp_IpAddr = VNPayUtil.getIpAddress(request);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);


        Map<String, String> sorted_vnp_Params = VNPayUtil.sortParams(vnp_Params);
        String hashData = VNPayUtil.buildHashData(sorted_vnp_Params);
        String vnp_SecureHash = VNPayUtil.hmacSHA512(vnpayProperties.getHashSecret(), hashData);


        String queryUrl = VNPayUtil.createQueryUrl(sorted_vnp_Params);

        String paymentUrl = vnpayProperties.getUrl() + "?" + queryUrl + "&vnp_SecureHash=" + vnp_SecureHash;

        return paymentUrl;
    }


    @Override
    public boolean handleVNPayReturn(Map<String, String> params) {

        String vnp_SecureHash = params.get("vnp_SecureHash");
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TxnRef = params.get("vnp_TxnRef");

        params.remove("vnp_SecureHash");
        Map<String, String> sortedParams = VNPayUtil.sortParams(params);
        String hashData;
        try {
            hashData = VNPayUtil.buildHashData(sortedParams);
        } catch (UnsupportedEncodingException e) {
            return false;
        }
        String generatedHash = VNPayUtil.hmacSHA512(vnpayProperties.getHashSecret(), hashData);

        if (!generatedHash.equals(vnp_SecureHash)) {
            return false;
        }

        PaymentTransaction transaction = transactionRepository.findByVnpayTxnRef(vnp_TxnRef)
                .orElse(null);

        if (transaction == null) {
            return false;
        }

        String currentStatus = transaction.getTransactionStatus();
        if ("SUCCESS".equals(currentStatus) || "FAILED".equals(currentStatus)) {
            return "00".equals(vnp_ResponseCode) && "SUCCESS".equals(currentStatus);
        }

        if ("00".equals(vnp_ResponseCode)) {

            // Cập nhật trạng thái và ngày thanh toán
            transaction.setTransactionStatus("SUCCESS");
            transaction.setPaymentDate(new Date());

            int durationInDays = transaction.getSubscriptionPlan().getDurationInDays();

            Calendar c = Calendar.getInstance();
            Date currentDate = getCurrentDateWithoutTime();
            Date newPremiumUntil = null; // Khởi tạo biến lưu ngày hết hạn

            boolean isCompanyFirstTimePurchase = false;

            if (transaction.getCandidate() != null) {

                Candidate candidate = transaction.getCandidate();
                Date currentPremiumUntil = candidate.getPremiumUntil();

                if (currentPremiumUntil == null || currentPremiumUntil.before(currentDate)) {
                    c.setTime(currentDate);
                } else {
                    c.setTime(currentPremiumUntil);
                }

                c.add(Calendar.DATE, durationInDays);
                newPremiumUntil = c.getTime();

                // Cập nhật Candidate và lưu
                candidate.setPremium(true);
                candidate.setStatus(true);
                candidate.setPremiumUntil(newPremiumUntil);
                candidateRepository.save(candidate);

            } else if (transaction.getAccountCompany() != null) {

                AccountCompany principalAccount = transaction.getAccountCompany();
                Date currentPremiumUntil = principalAccount.getPremiumUntil();

                isCompanyFirstTimePurchase = (currentPremiumUntil == null || currentPremiumUntil.before(currentDate));

                if (isCompanyFirstTimePurchase) {
                    c.setTime(currentDate);
                } else {
                    c.setTime(currentPremiumUntil);
                }

                c.add(Calendar.DATE, durationInDays);
                newPremiumUntil = c.getTime();

                // Cập nhật AccountCompany và lưu
                principalAccount.setPremium(true);
                principalAccount.setPremiumUntil(newPremiumUntil);
                principalAccount.setStatus(true);
                accountCompanyRepository.save(principalAccount);

            } else {
                return false;
            }


            if (newPremiumUntil != null) {
                transaction.setPremiumUntil(newPremiumUntil);
            }
            transactionRepository.save(transaction);


            if (transaction.getAccountCompany() != null) {
                AccountCompany principalAccount = transaction.getAccountCompany();


                if (!isCompanyFirstTimePurchase) {
                    unlockAndExtendSubAccounts(principalAccount, newPremiumUntil);
                }


                if (isCompanyFirstTimePurchase) {
                    createExtraAccounts(principalAccount, principalAccount.getCompany());
                }
            }

            return true;
        } else {


            transaction.setTransactionStatus("FAILED");
            transactionRepository.save(transaction);
            return false;
        }
    }

    /**
     * Unlock and extend premium time for sub-accounts upon principal account renewal.
     */
    @Transactional
    public void unlockAndExtendSubAccounts(AccountCompany principalAccount, Date newPremiumUntil) {
        String subEmailPrefix = "sub_%_" + principalAccount.getEmail();

        List<AccountCompany> subAccounts = accountCompanyRepository.findByCompanyAndEmailLike(
                principalAccount.getCompany(), subEmailPrefix
        );

        if (subAccounts != null && !subAccounts.isEmpty()) {
            for (AccountCompany subAccount : subAccounts) {
                subAccount.setStatus(true);
                subAccount.setPremium(true);
                subAccount.setPremiumUntil(newPremiumUntil);
            }
            accountCompanyRepository.saveAll(subAccounts);
        }
    }

    private Date getCurrentDateWithoutTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("Etc/GMT+7"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void deactivateExpiredPremiumAccounts() {

        Date currentDate = getCurrentDateWithoutTime();

        // 1. Process Candidate: Revert to regular account, DO NOT lock.
        List<Candidate> expiredCandidates = candidateRepository.findByIsPremiumTrueAndPremiumUntilBefore(currentDate);
        if (expiredCandidates != null && !expiredCandidates.isEmpty()) {
            for (Candidate candidate : expiredCandidates) {
                candidate.setPremium(false);
            }
            candidateRepository.saveAll(expiredCandidates);
        }

        // 2. Process AccountCompany (Principal Account): Revert to regular account, DO NOT lock principal account.
        List<AccountCompany> expiredPrincipalAccounts = accountCompanyRepository.findByIsPremiumTrueAndPremiumUntilBefore(currentDate);

        if (expiredPrincipalAccounts != null && !expiredPrincipalAccounts.isEmpty()) {
            for (AccountCompany principalAccount : expiredPrincipalAccounts) {
                principalAccount.setPremium(false);

                lockExpiredSubAccounts(principalAccount);
            }
            accountCompanyRepository.saveAll(expiredPrincipalAccounts);
        }
    }

    /**
     * Find and lock sub-accounts associated with the expired principal account.
     */
    @Transactional
    public void lockExpiredSubAccounts(AccountCompany principalAccount) {
        String subEmailPrefix = "sub_%_" + principalAccount.getEmail();

        List<AccountCompany> subAccounts = accountCompanyRepository.findByCompanyAndEmailLike(
                principalAccount.getCompany(), subEmailPrefix
        );

        if (subAccounts != null && !subAccounts.isEmpty()) {
            for (AccountCompany subAccount : subAccounts) {
                subAccount.setPremium(false); // Set premium status to false
                subAccount.setStatus(false); // LOCK SUB-ACCOUNT
            }
            accountCompanyRepository.saveAll(subAccounts);
        }
    }
}