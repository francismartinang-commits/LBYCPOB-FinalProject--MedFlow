import com.dlsu.medflow.model.Patient;
import com.dlsu.medflow.service.HospitalDataStore;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

/** Replaces the three tabs of {@code NurseDashboard}, plus its walk-in registration dialog. */
@Controller
@RequestMapping("/nurse")
public class NurseController {