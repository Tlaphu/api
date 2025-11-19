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
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

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
        System.out.println("Created 3 sub-accounts for company: " + principalAccount.getEmail());
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
        System.out.println("HASH DATA TO BE SIGNED: " + hashData);
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

        if ("00".equals(vnp_ResponseCode)) {

            transaction.setTransactionStatus("SUCCESS");
            transaction.setPaymentDate(new Date());
            transactionRepository.save(transaction);

            int durationInDays = transaction.getSubscriptionPlan().getDurationInDays();

            Calendar c = Calendar.getInstance();
            Date currentDate = getCurrentDateWithoutTime();

            if (transaction.getCandidate() != null) {

                Candidate candidate = transaction.getCandidate();
                Date currentPremiumUntil = candidate.getPremiumUntil();

                if (currentPremiumUntil == null || currentPremiumUntil.before(currentDate)) {
                    c.setTime(currentDate);
                } else {
                    c.setTime(currentPremiumUntil);
                }

                c.add(Calendar.DATE, durationInDays);
                Date newPremiumUntil = c.getTime();

                candidate.setPremium(true);
                candidate.setStatus(true); // Mở khóa/Đảm bảo active cho Candidate
                candidate.setPremiumUntil(newPremiumUntil);
                candidateRepository.save(candidate);

            } else if (transaction.getAccountCompany() != null) {

                AccountCompany principalAccount = transaction.getAccountCompany();
                Date currentPremiumUntil = principalAccount.getPremiumUntil();

                boolean isFirstTimePurchase = (currentPremiumUntil == null || currentPremiumUntil.before(currentDate));

                if (isFirstTimePurchase) {
                    c.setTime(currentDate);
                } else {
                    c.setTime(currentPremiumUntil);
                }

                c.add(Calendar.DATE, durationInDays);
                Date newPremiumUntil = c.getTime();

                principalAccount.setPremium(true);
                principalAccount.setPremiumUntil(newPremiumUntil);
                // MỞ KHÓA TÀI KHOẢN CÔNG TY KHI THANH TOÁN THÀNH CÔNG (Tài khoản chính luôn được mở khóa)
                principalAccount.setStatus(true);
                accountCompanyRepository.save(principalAccount);

                // Khi mua lại/gia hạn, cần đảm bảo 3 tài khoản phụ cũng được mở khóa và gia hạn theo tài khoản chính.
                if (!isFirstTimePurchase) {
                    unlockAndExtendSubAccounts(principalAccount, newPremiumUntil);
                }

                // CHỈ TẠO TÀI KHOẢN PHỤ NẾU LÀ LẦN MUA ĐẦU TIÊN
                if (isFirstTimePurchase) {
                    createExtraAccounts(principalAccount, principalAccount.getCompany());
                }

            } else {
                return false;
            }

            return true;
        } else {

            transaction.setTransactionStatus("FAILED");
            transactionRepository.save(transaction);
            return false;
        }
    }

    /**
     * Phương thức mới: Mở khóa và gia hạn thời gian cho các tài khoản phụ liên quan khi tài khoản chính gia hạn.
     */
    @Transactional
    public void unlockAndExtendSubAccounts(AccountCompany principalAccount, Date newPremiumUntil) {
        String subEmailPrefix = "sub_%_" + principalAccount.getEmail();

        // Tìm các tài khoản phụ có cùng Company và email theo mẫu sub_%_[email_chinh]
        List<AccountCompany> subAccounts = accountCompanyRepository.findByCompanyAndEmailLike(
                principalAccount.getCompany(), subEmailPrefix
        );

        if (subAccounts != null && !subAccounts.isEmpty()) {
            for (AccountCompany subAccount : subAccounts) {
                subAccount.setStatus(true); // Mở khóa
                subAccount.setPremium(true); // Đảm bảo Premium
                subAccount.setPremiumUntil(newPremiumUntil); // Gia hạn theo tài khoản chính
            }
            accountCompanyRepository.saveAll(subAccounts);
            System.out.println("Unlocked and extended " + subAccounts.size() + " sub-accounts for company: " + principalAccount.getEmail());
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
        System.out.println("Starting scheduled task: Deactivating expired premium accounts...");

        Date currentDate = getCurrentDateWithoutTime();

        // 1. Xử lý Candidate: Chuyển về tài khoản thường, KHÔNG khóa.
        List<Candidate> expiredCandidates = candidateRepository.findByIsPremiumTrueAndPremiumUntilBefore(currentDate);
        if (expiredCandidates != null && !expiredCandidates.isEmpty()) {
            for (Candidate candidate : expiredCandidates) {
                candidate.setPremium(false);
            }
            candidateRepository.saveAll(expiredCandidates);
            System.out.printf("Deactivated %d expired Candidate accounts to regular status.\n", expiredCandidates.size());
        }

        // 2. Xử lý AccountCompany (Tài khoản chính): Chuyển về tài khoản thường, KHÔNG khóa tài khoản chính.
        List<AccountCompany> expiredPrincipalAccounts = accountCompanyRepository.findByIsPremiumTrueAndPremiumUntilBefore(currentDate);

        if (expiredPrincipalAccounts != null && !expiredPrincipalAccounts.isEmpty()) {
            for (AccountCompany principalAccount : expiredPrincipalAccounts) {
                principalAccount.setPremium(false);
                // KHÔNG KHÓA TÀI KHOẢN CHÍNH

                // TÌM VÀ KHÓA 3 TÀI KHOẢN PHỤ
                lockExpiredSubAccounts(principalAccount);
            }
            accountCompanyRepository.saveAll(expiredPrincipalAccounts);
            System.out.printf("Deactivated premium status for %d expired principal AccountCompany accounts.\n", expiredPrincipalAccounts.size());
        }

        System.out.println("Scheduled task completed.");
    }

    /**
     * Phương thức mới: Tìm và khóa 3 tài khoản phụ liên quan đến tài khoản chính đã hết hạn.
     */
    @Transactional
    public void lockExpiredSubAccounts(AccountCompany principalAccount) {
        String subEmailPrefix = "sub_%_" + principalAccount.getEmail();

        // Tìm các tài khoản phụ có cùng Company và email theo mẫu sub_%_[email_chinh]
        List<AccountCompany> subAccounts = accountCompanyRepository.findByCompanyAndEmailLike(
                principalAccount.getCompany(), subEmailPrefix
        );

        if (subAccounts != null && !subAccounts.isEmpty()) {
            for (AccountCompany subAccount : subAccounts) {
                subAccount.setPremium(false); // Đặt trạng thái premium = false
                subAccount.setStatus(false); // KHÓA TÀI KHOẢN PHỤ
            }
            accountCompanyRepository.saveAll(subAccounts);
            System.out.println("Locked " + subAccounts.size() + " sub-accounts for company: " + principalAccount.getEmail());
        }
    }
}