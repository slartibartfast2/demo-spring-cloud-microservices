package ea.slartibarfast.payment.repository;

import ea.slartibarfast.payment.model.Payment;

import java.util.ArrayList;
import java.util.List;

public class PaymentRepository {

    private static List<Payment> payments = new ArrayList<>();

    public Payment add(Payment payment) {
        payment.setId((long) (payments.size() + 1));
        payments.add(payment);
        return payment;
    }

    public Payment findById(Long id) {
        return payments.stream().filter(p -> p.getId().equals(id)).findFirst().orElse(null);
    }

    public List<Payment> findAll() {
        return payments;
    }

}
