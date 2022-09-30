package ea.slartibartfast.payment.manager;

import ea.slartibartfast.payment.controller.converter.CreatePaymentRequestToPaymentConverter;
import ea.slartibartfast.payment.controller.converter.PaymentToRetrievePaymentResponseConverter;
import ea.slartibartfast.payment.controller.converter.PaymentToRetrievePaymentResponseWithCardConverter;
import ea.slartibartfast.payment.controller.request.CreatePaymentRequest;
import ea.slartibartfast.payment.controller.response.CreatePaymentResponse;
import ea.slartibartfast.payment.controller.response.RetrievePaymentResponse;
import ea.slartibartfast.payment.controller.response.RetrievePaymentResponseWithCard;
import ea.slartibartfast.payment.model.Payment;
import ea.slartibartfast.payment.repository.PaymentRepository;
import ea.slartibartfast.payment.service.CardIntegrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class PaymentManager {

    private final PaymentToRetrievePaymentResponseWithCardConverter paymentToRetrievePaymentResponseWithCardConverter;
    private final PaymentToRetrievePaymentResponseConverter paymentToRetrievePaymentResponseConverter;
    private final CreatePaymentRequestToPaymentConverter createPaymentRequestToPaymentConverter;
    private final PaymentRepository paymentRepository;
    private final CardIntegrationService cardIntegrationService;

    public RetrievePaymentResponse retrievePayment(Long id) {
        Payment payment = paymentRepository.findById(id);
        return paymentToRetrievePaymentResponseConverter.apply(payment);
    }

    public List<RetrievePaymentResponse> retrieveAllPayments() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(paymentToRetrievePaymentResponseConverter).collect(Collectors.toList());
    }

    public CreatePaymentResponse createPayment(CreatePaymentRequest createPaymentRequest) {
        Payment payment = createPaymentRequestToPaymentConverter.apply(createPaymentRequest);
        paymentRepository.add(payment);
        return CreatePaymentResponse.builder().status("success").build();
    }

    public RetrievePaymentResponseWithCard retrievePaymentWithCard(Long id, int delay, int faultPercent) {
        Payment payment = paymentRepository.findById(id);
        payment.setCard(cardIntegrationService.retrievePaymentWithCard(id, delay, faultPercent));
        return paymentToRetrievePaymentResponseWithCardConverter.apply(payment);
    }
}
