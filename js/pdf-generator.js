import { jsPDF } from 'jspdf';

export function generateContactCardPDF({
  name = 'Alex Morgan',
  title = 'Product Designer',
  email = 'alex@taplink.io',
  phone = '+1 (555) 123-4567',
  website = 'taplink.io/alex',
  linkedin = 'linkedin.com/in/alexmorgan',
} = {}) {
  const doc = new jsPDF({ orientation: 'landscape', unit: 'mm', format: [90, 55] });

  doc.setFillColor(15, 15, 25);
  doc.rect(0, 0, 90, 55, 'F');

  const gradientSteps = 20;
  for (let i = 0; i < gradientSteps; i++) {
    const ratio = i / gradientSteps;
    const r = Math.round(99 + (168 - 99) * ratio);
    const g = Math.round(102 + (85 - 102) * ratio);
    const b = Math.round(241 + (247 - 241) * ratio);
    doc.setFillColor(r, g, b);
    doc.rect(0, 0 + i * (3 / gradientSteps), 90, 3 / gradientSteps, 'F');
  }

  doc.setFillColor(255, 255, 255, 0.06);
  doc.roundedRect(62, 6, 22, 16, 2, 2, 'F');

  doc.setTextColor(255, 255, 255);
  doc.setFontSize(6);
  doc.setFont('helvetica', 'normal');
  doc.text('NFC', 73, 16, { align: 'center' });

  doc.setTextColor(255, 255, 255);
  doc.setFontSize(11);
  doc.setFont('helvetica', 'bold');
  doc.text(name, 8, 22);

  doc.setTextColor(160, 160, 190);
  doc.setFontSize(7);
  doc.setFont('helvetica', 'normal');
  doc.text(title.toUpperCase(), 8, 27);

  doc.setDrawColor(60, 60, 80);
  doc.setLineWidth(0.2);
  doc.line(8, 31, 82, 31);

  doc.setTextColor(200, 200, 220);
  doc.setFontSize(6);

  doc.setFont('helvetica', 'bold');
  doc.setTextColor(140, 140, 170);
  doc.text('EMAIL', 8, 36);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(200, 200, 220);
  doc.text(email, 8, 39.5);

  doc.setFont('helvetica', 'bold');
  doc.setTextColor(140, 140, 170);
  doc.text('PHONE', 50, 36);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(200, 200, 220);
  doc.text(phone, 50, 39.5);

  doc.setFont('helvetica', 'bold');
  doc.setTextColor(140, 140, 170);
  doc.text('WEB', 8, 44.5);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(200, 200, 220);
  doc.text(website, 8, 48);

  doc.setFont('helvetica', 'bold');
  doc.setTextColor(140, 140, 170);
  doc.text('LINKEDIN', 50, 44.5);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(200, 200, 220);
  doc.text(linkedin, 50, 48);

  doc.setTextColor(99, 102, 241);
  doc.setFontSize(5);
  doc.setFont('helvetica', 'bold');
  doc.text('TAPLINK', 73, 52, { align: 'center' });

  doc.save(`${name.replace(/\s+/g, '_')}_TapLink_Card.pdf`);
}
