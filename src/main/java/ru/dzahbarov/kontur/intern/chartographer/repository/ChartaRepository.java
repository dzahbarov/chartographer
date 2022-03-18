package ru.dzahbarov.kontur.intern.chartographer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.dzahbarov.kontur.intern.chartographer.domain.Charta;

/**
 * @author dzahbarov
 */

@Repository
public interface ChartaRepository extends JpaRepository<Charta, Long> {

}
