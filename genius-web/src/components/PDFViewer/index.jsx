/*
 * @Description:
 * @Version: 2.0
 * @Autor: jl.g
 * @Date: 2023-07-08 12:50:55
 * @LastEditors: jl.g
 * @LastEditTime: 2023-07-11 14:54:54
 */
import React, { useState, useEffect } from "react";
import { Document, Page, pdfjs } from "react-pdf";
import demoPdf from "./b.pdf";
import "react-pdf/dist/esm/Page/AnnotationLayer.css";
import "./index.less";
pdfjs.GlobalWorkerOptions.workerSrc = `//cdnjs.cloudflare.com/ajax/libs/pdf.js/${pdfjs.version}/pdf.worker.js`;
function PDFViewer({ pdfData }) {
  const [windowHeight, setWindowHeight] = useState(0);
  const [numPages, setNumPages] = useState(null);
  const [pageNumber, setPageNumber] = useState(1);
  const [pageInput, setPageInput] = useState(1);
  const [data, setData] = useState(null);
  const [scale, setScale] = useState(1.6);

  const onDocumentLoadSuccess = ({ numPages }) => {
    setNumPages(numPages);
  };
  useEffect(() => {
    function init() {
      setNumPages(null);
      setPageNumber(1);
      setPageInput(1);
      setData(null);
    }

    if (!pdfData) return;
    init();

    setWindowHeight(window.innerHeight);
  }, []);

  return (
    <div
      className="PDFViewerWarp"
      style={{ maxHeight: (windowHeight / 4) * 3 }}
    >
      <Document file={demoPdf} onLoadSuccess={onDocumentLoadSuccess}>
        <Page pageNumber={pageNumber} />
      </Document>
      <p>
        Page {pageNumber} of {numPages}
      </p>
    </div>
  );
}

export default PDFViewer;
