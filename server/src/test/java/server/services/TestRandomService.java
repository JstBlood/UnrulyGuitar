package server.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@SuppressWarnings("serial")
@Service
public class TestRandomService extends Random {
    public boolean wasCalled = false;

    @Override
    public int nextInt(int bound) {
        wasCalled = true;
        return super.nextInt();
    }
}
