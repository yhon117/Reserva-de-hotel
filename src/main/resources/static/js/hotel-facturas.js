var facturasTable = null;

$(document).ready(function () {
  if (!requireAuth()) return;
  setActiveSidebar('facturas');
  loadFacturas();
});

function loadFacturas() {
  api.getFacturas().then(function (data) {
    renderFacturasTable(Array.isArray(data) ? data : []);
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}

function getEstadoFacturaBadge(pagada) {
  return pagada
    ? '<span class="badge badge-success">Pagada</span>'
    : '<span class="badge badge-warning">Pendiente</span>';
}

function renderFacturasTable(data) {
  var rows = data.map(function (f) {
    var acciones =
      '<button class="btn btn-sm btn-info btn-ver-factura" data-id="' + f.id + '"><i class="fas fa-eye"></i></button> ' +
      '<a href="' + api.getFacturaPdfUrl(f.id) + '" class="btn btn-sm btn-primary" target="_blank"><i class="fas fa-file-pdf"></i></a>';

    return [
      f.numeroFactura || '—',
      f.reservaId || '—',
      f.huespedNombre || '—',
      f.habitacionNumero || '—',
      '$' + (f.total || 0).toFixed(2),
      getEstadoFacturaBadge(f.pagada),
      f.fechaEmision ? new Date(f.fechaEmision).toLocaleDateString('es-ES') : '—',
      acciones
    ];
  });

  if (facturasTable) {
    facturasTable.clear().rows.add(rows).draw();
    return;
  }

  facturasTable = $('#dataTable').DataTable({
    data: rows,
    columns: [
      { title: 'N° Factura' }, { title: 'Reserva' }, { title: 'Huésped' },
      { title: 'Habitación' }, { title: 'Total' }, { title: 'Estado' },
      { title: 'Fecha Emisión' }, { title: 'Acciones', orderable: false }
    ],
    language: { url: '//cdn.datatables.net/plug-ins/1.13.7/i18n/es-ES.json' },
    order: [[0, 'desc']]
  });

  $('#dataTable tbody').on('click', '.btn-ver-factura', function () {
    var id = parseInt($(this).data('id'));
    verDetalleFactura(id);
  });
}

function verDetalleFactura(id) {
  api.getFactura(id).then(function (f) {
    var itemsHtml = '';
    if (f.items && f.items.length > 0) {
      itemsHtml = '<h6>Detalle</h6><table class="table table-sm"><thead><tr><th>Descripción</th><th>Cant.</th><th>P. Unit.</th><th>Total</th></tr></thead><tbody>';
      f.items.forEach(function (item) {
        itemsHtml += '<tr><td>' + item.descripcion + '</td><td>' + item.cantidad + '</td><td>$' + item.precioUnitario.toFixed(2) + '</td><td>$' + item.total.toFixed(2) + '</td></tr>';
      });
      itemsHtml += '</tbody></table>';
    }

    var bodyHtml =
      '<div class="row"><div class="col-md-6">' +
      '<p><strong>N° Factura:</strong> ' + f.numeroFactura + '</p>' +
      '<p><strong>Fecha:</strong> ' + new Date(f.fechaEmision).toLocaleString('es-ES') + '</p>' +
      '</div><div class="col-md-6">' +
      '<p><strong>Huésped:</strong> ' + f.huespedNombre + '</p>' +
      '<p><strong>Email:</strong> ' + (f.huespedEmail || '—') + '</p>' +
      '</div></div><hr>' +
      '<div class="row"><div class="col-md-6">' +
      '<p><strong>Habitación:</strong> #' + f.habitacionNumero + ' (' + (f.habitacionTipo || '') + ')</p>' +
      '<p><strong>Entrada:</strong> ' + new Date(f.fechaEntrada).toLocaleDateString('es-ES') + '</p>' +
      '<p><strong>Salida:</strong> ' + new Date(f.fechaSalida).toLocaleDateString('es-ES') + '</p>' +
      '<p><strong>Noches:</strong> ' + (f.noches || 0) + '</p>' +
      '</div><div class="col-md-6">' +
      itemsHtml +
      '<hr>' +
      '<p><strong>Subtotal:</strong> $' + (f.subtotal || 0).toFixed(2) + '</p>' +
      '<p><strong>IVA (19%):</strong> $' + (f.iva || 0).toFixed(2) + '</p>' +
      '<h5><strong>Total:</strong> $' + (f.total || 0).toFixed(2) + '</h5>' +
      '<p><strong>Estado:</strong> ' + getEstadoFacturaBadge(f.pagada) + '</p>' +
      '</div></div>';

    $('#modalFacturaTitle').text('Factura ' + f.numeroFactura);
    $('#modalFacturaBody').html(bodyHtml);
    $('#modalFactura').modal('show');
  }).catch(function (err) {
    alert('Error: ' + err.message);
  });
}
