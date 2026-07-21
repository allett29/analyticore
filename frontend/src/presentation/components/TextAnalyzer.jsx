/**
 * Capa de Presentación — Formulario de entrada de texto.
 */
export default function TextAnalyzer({ text, onTextChange, onSubmit, loading }) {
  return (
    <form className="analyzer-form" onSubmit={onSubmit}>
      <label htmlFor="text-input">Introduce el texto a analizar</label>
      <textarea
        id="text-input"
        value={text}
        onChange={(e) => onTextChange(e.target.value)}
        placeholder="Escribe o pega un texto aquí..."
        rows={6}
        required
        disabled={loading}
      />
      <button type="submit" disabled={loading || !text.trim()}>
        {loading ? 'Analizando...' : 'Analizar texto'}
      </button>
    </form>
  )
}
