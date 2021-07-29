package ea.slartibarfast.payment.manager;

import ea.slartibarfast.payment.client.CardClient;
import ea.slartibarfast.payment.controller.converter.CreatePaymentRequestToPaymentConverter;
import ea.slartibarfast.payment.controller.converter.PaymentToRetrievePaymentResponseConverter;
import ea.slartibarfast.payment.controller.converter.PaymentToRetrievePaymentResponseWithCardConverter;
import ea.slartibarfast.payment.controller.request.CreatePaymentRequest;
import ea.slartibarfast.payment.controller.response.CreatePaymentResponse;
import ea.slartibarfast.payment.controller.response.RetrievePaymentResponse;
import ea.slartibarfast.payment.controller.response.RetrievePaymentResponseWithCard;
import ea.slartibarfast.payment.model.Payment;
import ea.slartibarfast.payment.repository.PaymentRepository;
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
    private final CardClient cardClient;

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
        return new CreatePaymentResponse();
    }

    public RetrievePaymentResponseWithCard retrievePaymentWithCard(Long id) {
        Payment payment = paymentRepository.findById(id);
        payment.setCard(cardClient.findByPayment(id));
        return paymentToRetrievePaymentResponseWithCardConverter.apply(payment);
    }
}
