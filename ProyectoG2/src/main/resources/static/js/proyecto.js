
(function () {
    const ok = document.body.dataset.ok;
    const err = document.body.dataset.err;

    if (ok) {
        Swal.fire({
            icon: 'success',
            title: ok,
            timer: 1800,
            showConfirmButton: false
        });
    }

    if (err) {
        Swal.fire({
            icon: 'error',
            title: err,
            timer: 2200,
            showConfirmButton: false
        });
    }

    document.querySelectorAll('.form-eliminar').forEach(form => {
        form.addEventListener('submit', function (e) {
            e.preventDefault();
            const nombre = this.dataset.nombre || 'este producto';
            const id = this.dataset.id || '';

            Swal.fire({
                title: '¿Eliminar?',
                html: `Se eliminará <b>${nombre}</b> (ID ${id}).`,
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar',
                reverseButtons: true,
                focusCancel: true
            }).then(res => {
                if (res.isConfirmed)
                    this.submit();
            });
        });
    });
})();


