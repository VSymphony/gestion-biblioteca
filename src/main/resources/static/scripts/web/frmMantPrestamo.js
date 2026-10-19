$(document).ready(function () {
    cargarDatos();
});

function cargarDatos() {
    $.ajax({
        url: '/prestamo/usuarios',
        type: 'GET',
        dataType: 'json'
    }).done(function (resp) {
        var sel = $('#usuarioPrestamo');
        sel.empty();
        (resp.data || []).forEach(function (u) {
            sel.append($('<option>').val(u.idusuario).text(u.nomusuario));
        });
    });
    $.ajax({
        url: '/prestamo/libros',
        type: 'GET',
        dataType: 'json'
    }).done(function (resp) {
        var sel = $('#libroPrestamo');
        sel.empty();
        (resp.data || []).forEach(function (l) {
            sel.append($('<option>').val(l.idlibro).text(l.titulo + ' (stock ' + l.stock + ')'));
        });
    });
    listarPrestamos();
}

function listarPrestamos() {
    $.ajax({
        url: '/prestamo/listar',
        type: 'GET',
        dataType: 'json'
    }).done(function (resp) {
        var tbody = $('#tablaPrestamos tbody');
        tbody.empty();
        (resp.data || []).forEach(function (p) {
            tbody.append(filaPrestamo(p));
        });
    }).fail(function () {
        mostrarMensaje('#mensajePrestamo', 'Error al listar los préstamos', false);
    });
}

function filaPrestamo(p) {
    var estado = estadoPrestamo(p);
    var acciones = $('<td>');
    if (!p.fechadevuelto) {
        acciones.append($('<button>')
            .addClass('btn btn-sm btn-success')
            .text('Devolver')
            .on('click', function () { devolverPrestamo(p.idprestamo); }));
    }
    return $('<tr>')
        .append($('<td>').text(p.idprestamo))
        .append($('<td>').text(p.libro ? p.libro.titulo : ''))
        .append($('<td>').text(p.usuario ? p.usuario.nomusuario : ''))
        .append($('<td>').text(formatearFecha(p.fechaprestamo)))
        .append($('<td>').text(formatearFecha(p.fechadevolucion)))
        .append($('<td>').append($('<span>').addClass('badge ' + estado.clase).text(estado.texto)))
        .append($('<td>').text(formatearFecha(p.fechadevuelto)))
        .append(acciones);
}

function estadoPrestamo(p) {
    if (p.fechadevuelto) {
        return { texto: 'Devuelto', clase: 'text-bg-success' };
    }
    if (p.fechadevolucion && p.fechadevolucion < hoyLocal()) {
        return { texto: 'Vencido', clase: 'text-bg-danger' };
    }
    return { texto: 'Prestado', clase: 'text-bg-warning' };
}

function hoyLocal() {
    var d = new Date();
    var mes = ('0' + (d.getMonth() + 1)).slice(-2);
    var dia = ('0' + d.getDate()).slice(-2);
    return d.getFullYear() + '-' + mes + '-' + dia;
}

function formatearFecha(fecha) {
    if (!fecha) {
        return '';
    }
    var partes = fecha.split('-');
    return partes.length === 3 ? partes[2] + '/' + partes[1] + '/' + partes[0] : fecha;
}

function nuevoPrestamo() {
    bootstrap.Modal.getOrCreateInstance(document.getElementById('modalPrestamo')).show();
}

function grabarPrestamo() {
    $.ajax({
        url: '/prestamo/grabar',
        type: 'POST',
        data: {
            idusuario: $('#usuarioPrestamo').val(),
            idlibro: $('#libroPrestamo').val(),
            _csrf: csrfToken()
        },
        dataType: 'json'
    }).done(function (resp) {
        mostrarMensaje('#mensajePrestamo', resp.mensaje, resp.respuesta);
        if (resp.respuesta) {
            bootstrap.Modal.getOrCreateInstance(document.getElementById('modalPrestamo')).hide();
            cargarDatos();
        }
    }).fail(function () {
        mostrarMensaje('#mensajePrestamo', 'Error al registrar el préstamo', false);
    });
}

function devolverPrestamo(idprestamo) {
    if (!confirm('¿Registrar la devolución del préstamo?')) {
        return;
    }
    $.ajax({
        url: '/prestamo/devolver',
        type: 'POST',
        data: {
            idprestamo: idprestamo,
            _csrf: csrfToken()
        },
        dataType: 'json'
    }).done(function (resp) {
        mostrarMensaje('#mensajePrestamo', resp.mensaje, resp.respuesta);
        if (resp.respuesta) {
            cargarDatos();
        }
    }).fail(function () {
        mostrarMensaje('#mensajePrestamo', 'Error al registrar la devolución', false);
    });
}
