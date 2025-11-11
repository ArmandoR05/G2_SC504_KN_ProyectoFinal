
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

document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.btn-eliminar').forEach(btn => {
        btn.addEventListener('click', function () {
            const form = this.closest('form');
            Swal.fire({
                title: '¿Eliminar registro?',
                text: 'Esta acción no se puede deshacer.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'Sí, eliminar',
                cancelButtonText: 'Cancelar',
                reverseButtons: true
            }).then((result) => {
                if (result.isConfirmed)
                    form.submit();
            });
        });
    });
});

const okMsg = /*[[${ok}]]*/ null;
if (okMsg) {
    Swal.fire({icon: 'success', title: '¡Listo!', text: okMsg, timer: 1800, showConfirmButton: false});
}

// Validación Bootstrap simple
(function () {
    const form = document.getElementById('frm-crear-cliente');
    form.addEventListener('submit', function (e) {
        if (!form.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        form.classList.add('was-validated');
    }, false);
})();


document.querySelectorAll('.btn-eliminar').forEach(btn => {
    btn.addEventListener('click', function () {
        const form = this.closest('form');
        Swal.fire({
            title: '¿Eliminar cliente?',
            text: 'Esta acción no se puede deshacer.',
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Sí, eliminar',
            cancelButtonText: 'Cancelar',
            reverseButtons: true
        }).then((result) => {
            if (result.isConfirmed) {
                form.submit();
            }
        });
    });
});


