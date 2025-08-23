package org.assistantAPI.repository;

import org.assistantAPI.domain.*;
import org.springframework.data.jpa.repository.*;
import java.util.*;

public interface SellerRepo extends JpaRepository<Seller, UUID> {}